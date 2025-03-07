package com.cmi.simu.flow;

import java.util.*;

/**
 * Coordonne la simulation:
 * - Itération sur tous les services
 * - Calcul des flux potentiels
 * - Sélection des patients à transférer
 * - Application du temps de traitement + sortie si soignés
 * - Absorption
 * - Gérer la saturation et éventuellement le refus
 */
public class FlowSimulator {

    private final List<HospitalUnit> units;
    private final FlowManager flowManager;

    public FlowSimulator(List<HospitalUnit> units, FlowManager flowManager) {
        this.units = units;
        this.flowManager = flowManager;
    }

    /**
     * Effectue un pas de simulation :
     * 1) Chaque unité accepte d'abord les arrivées extérieures.
     * 2) On décrémente le temps de traitement de chaque patient,
     * et on vérifie s'ils doivent sortir (si timeToTreat=0, ils sortent du système).
     * 3) On calcule les flux potentiels i→j et on forme des listes de patients transférables.
     * 4) On transfère effectivement les patients (en respectant staffCapacity, maxCapacity).
     * 5) On applique l'absorption (patients qui sortent naturellement).
     *
     * @return Ceux qui sont sortis de l'hopital
     */
    public Map<String, Integer> simulateOneStep() {

        Map<String, Integer> sortis = new HashMap<>();

        // 1) Arrivées extérieures + la map
        for (HospitalUnit unit : units) {
            System.out.println("Accepted arrivals : " + unit.acceptExternalArrivals());
            sortis.put(unit.getName(), 0);
        }

        // 2) Mettre à jour le temps de traitement / sortie définitive
        for (HospitalUnit unit : units) {
//            Iterator<Patient> it = unit.getPatients().iterator();
//            while (it.hasNext()) {
//                Patient p = it.next();
//                // On incrémente le temps passé
//                p.incrementTimeInService();
//                // On décrémente la durée de traitement
//                p.decreaseTimeToTreat();
//                // Si le patient a fini son traitement, il sort du système
//                if (p.getTimeToTreat() <= 0) {
//                    it.remove();
//                    sortis.merge(unit.getName(), 1, Integer::sum);
//                }
//            }

            int outDueToTreatment = unit.treatPatientsOneStep();
            sortis.merge(unit.getName(), outDueToTreatment, Integer::sum);
        }

        // 3) Calculer les flux potentiels
        // On crée une structure pour stocker la liste de patients à transférer : (unit i → listOfPatients → j)
        Map<String, List<Patient>> transferMap = new HashMap<>();

        for (HospitalUnit i : units) {
            if (i.isObstacle() || i.getPatients().isEmpty()) continue;

            int totalPatients = i.getPatients().size();

            // On calcule la somme des flux potentiels vers chaque voisin
            double totalFluxSum = 0.0;
            List<HospitalUnit> neighbors = i.getNeighbors();
            Map<HospitalUnit, Double> neighborFlux = new HashMap<>();

            for (HospitalUnit j : neighbors) {
                double fluxValue = flowManager.computeFlux(i, j);
                if (fluxValue > 0) {
                    neighborFlux.put(j, fluxValue);
                    totalFluxSum += fluxValue;
                }
            }

            if (totalFluxSum <= 0) {
                // pas de flux sortant
                continue;
            }

            // On répartit les patients de i vers j proportionnellement au fluxValue
            // en donnant la priorité d'abord aux patients URGENT, puis NORMAL, ensuite LOW
            // → On trie la liste i.getPatients() par priorité
            List<Patient> sortedPatients = new ArrayList<>(i.getPatients());
            sortedPatients.sort((p1, p2) -> p1.getPriority().compareTo(p2.getPriority()));

            // On construit la distribution
            for (HospitalUnit j : neighborFlux.keySet()) {
                double fraction = neighborFlux.get(j) / totalFluxSum;
                int nbTransfer = (int) Math.floor(fraction * totalPatients);

                // On va chercher dans sortedPatients, en partant par la plus haute priority
                // (Si on considère URGENT < NORMAL < LOW comme ordre, il faut ajuster le comparateur.)
                // Pour clarifier, je suppose ici URGENT < NORMAL < LOW → on transfère d'abord LOW.
                // OU l'inverse, selon la logique souhaitée.

                // Ex : on transfère les patients de plus "basse" priorité en premier ou l'inverse.
                // Choisissons : on transfère d'abord les LOW, puis NORMAL, ensuite URGENT (l'idée : URGENT reste plus longtemps).
                // À toi d'ajuster la logique souhaitée.

                // On va simplifier en transférant "nbTransfer" patients depuis la queue de sortedPatients
                List<Patient> subListToTransfer = new ArrayList<>();

                // On prend nbTransfer patients depuis la fin (lowest priority) ou le début (highest) 
                // selon la politique. Mettons qu'on transfère d'abord la "faible" priorité.
                for (int c = 0; c < nbTransfer && !sortedPatients.isEmpty(); c++) {
                    // On prend le dernier (faible prio).
                    Patient p = sortedPatients.removeLast();
                    subListToTransfer.add(p);
                }

                // Stockage dans transferMap
                String key = buildKey(i, j);
                transferMap.computeIfAbsent(key, k -> new ArrayList<>()).addAll(subListToTransfer);
            }
        }

        // 4) Appliquer les transferts
        // On retire les patients de leur unité source et on tente de les ajouter à la destination
        for (String key : transferMap.keySet()) {
            List<Patient> listToTransfer = transferMap.get(key);
            if (listToTransfer.isEmpty()) continue;

            HospitalUnit source = parseSource(key);
            HospitalUnit target = parseTarget(key);

            // Vérif staff capacity du target
            // On considère qu'à chaque itération, target ne peut prendre que staffCapacity patients en plus
            // (ou on peut ignorer staff capacity si on suppose qu'il s'applique seulement au traitement)
            int canAccept = target.getMaxCapacity() - target.getPatients().size();
            // On peut affiner en tenant compte du staffCapacity. 
            // Ex: staffCapacity = nb de patients traités, 
            // mais on peut limiter la prise de nouveaux patients si staff saturé...

            // Couper la liste à canAccept
            List<Patient> actuallyTransferred = new ArrayList<>();
            for (int i = 0; i < listToTransfer.size() && i < canAccept; i++) {
                actuallyTransferred.add(listToTransfer.get(i));
            }

            // Retrait du source
            for (Patient p : actuallyTransferred) {
                source.removePatient(p);
            }
            // Ajout au target
            for (Patient p : actuallyTransferred) {
                target.addPatient(p);
            }
            // (Si plus de patients que canAccept, ils restent dans la source,
            //  ou on les envoie ailleurs → à affiner selon la logique.)
        }

        // 5) Absorption dans chaque unité
        for (HospitalUnit u : units) {
            sortis.merge(u.getName(), u.applyAbsorption(), Integer::sum);
        }

        return sortis;
    }

    /**
     * Lance la simulation sur nbSteps étapes.
     */
    public Map<String, Integer> runOneStep(ArrivalScenario scenario, List<HospitalUnit> units) {

        // Met à jour externalArrivals dans chaque unité
        // selon le scénario (ex. t=10 → afflux massif)
        scenario.updateArrivals();

        Map<String, Integer> sortis = simulateOneStep();

        int totalPatients = 0;
        for (HospitalUnit u : units) {
            int load = u.getCurrentLoad();
            totalPatients += load;
            System.out.printf("  %s: load=%d (URGENT=%d, NORMAL=%d, LOW=%d)\n",
                    u.getName(),
                    load,
                    countPriority(u.getPatients(), PriorityLevel.URGENT),
                    countPriority(u.getPatients(), PriorityLevel.NORMAL),
                    countPriority(u.getPatients(), PriorityLevel.LOW)
            );
        }
        System.out.println(" -> Total Patient = " + totalPatients);
        System.out.println("--------------------------------");

        return sortis;
    }

    // --- Outils pour transferMap ---
    private String buildKey(HospitalUnit i, HospitalUnit j) {
        return i.getName() + "->" + j.getName();
    }

    private HospitalUnit parseSource(String key) {
        String[] parts = key.split("->");
        return findUnit(parts[0]);
    }

    private HospitalUnit parseTarget(String key) {
        String[] parts = key.split("->");
        return findUnit(parts[1]);
    }

    private HospitalUnit findUnit(String name) {
        for (HospitalUnit u : units) {
            if (u.getName().equals(name)) {
                return u;
            }
        }
        return null;
    }

    private static int countPriority(List<Patient> plist, PriorityLevel prio) {
        int c = 0;
        for (Patient p : plist) {
            if (p.getPriority() == prio) {
                c++;
            }
        }
        return c;
    }
}