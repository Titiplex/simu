package com.cmi.simu.flow;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFlow {
    // Définition des services
    private static final String[] SERVICES = {"Urgences", "Chirurgie", "Medecine", "Reeducation", "ZoneBloquee"};

    // Stockage des charges par service
    @Getter
    private static Map<String, Double> charges = new HashMap<>();
    @Getter
    private static int totalPatients = 0;

    public static void setCharges(Map<String, Double> charges) {
        MainFlow.charges = charges;
    }

    public static void setTotalPatients(int totalPatients) {
        MainFlow.totalPatients = totalPatients;
    }

    public static void main(String[] args) {

        // --- 1) Création de quelques unités (services hospitaliers) ---
        List<HospitalUnit> units = getHospitalUnits();

        // --- 4) Créer le FlowManager (configurer k, k_lat, epsilon) ---
        // k = 1.0 (flux gravitaire),
        // k_lat = 0.3 (diffusion latérale plus lente),
        // epsilon = 2.0
        FlowManager flowManager = new FlowManager(1.0, 0.3, 2.0);

        // --- 5) Créer le FlowSimulator ---
        FlowSimulator simulator = new FlowSimulator(units, flowManager);

        // --- 6) Gérer un scénario d'arrivées variables ---
        ArrivalScenario scenario = new ArrivalScenario(units);

        // --- 7) Lancer la simulation pas à pas, en mettant à jour les arrivées ---

        // Initialisation des charges
        for (String service : SERVICES) {
            charges.put(service, 0.0);
        }

        // Simulation sur 20 unités de temps
        for (int time = 0; time < 20; time++) {
            System.out.println("=== Time " + time + " ===");

            // Simule l'entrée de nouveaux patients aux Urgences
            int nouveauxPatients = (int) (Math.random() * 5 + 1); // Entre 1 et 5 patients par unité de temps
            totalPatients += (int) nouveauxPatients;
            charges.put("Urgences", charges.get("Urgences") + nouveauxPatients);
            log("Arrivee", "Urgences", nouveauxPatients);

            // Déplacement des patients entre services
            transfererPatients("Urgences", "Chirurgie", 2.0);
            transfererPatients("Chirurgie", "Medecine", 3.0);
            transfererPatients("Medecine", "Reeducation", 2.0);

            // Sortie des patients de Reeducation
            double sortants = Math.min(charges.get("Reeducation"), 3.0); // 3 patients max peuvent sortir
            charges.put("Reeducation", charges.get("Reeducation") - sortants);
            totalPatients -= (int) sortants;
            log("Sortie", "Reeducation", sortants);

            // Affichage des charges actuelles
            afficherCharges();

            // Affichage du nombre total de patients
            System.out.println("Total patients dans l'hopital : " + totalPatients);
            System.out.println();
        }


        System.out.println("Simulation terminee !");
    }

    @NotNull
    private static List<HospitalUnit> getHospitalUnits() {
        HospitalUnit urgences = new HospitalUnit("Urgences", 15.0, 0.05, 50.0, false);
        HospitalUnit chirurgie = new HospitalUnit("Chirurgie", 10.0, 0.02, 30.0, false);
        HospitalUnit medecine = new HospitalUnit("Medecine", 9.0, 0.01, 40.0, false);
        HospitalUnit rehab = new HospitalUnit("Reeducation", 5.0, 0.01, 20.0, false);
        HospitalUnit blocage = new HospitalUnit("ZoneBloquee", 0.0, 0.00, 0.0, true);

        // --- 2) Définir les voisinages (graphe) ---
        // Admettons :
        // Urgences <-> Chirurgie,
        // Chirurgie <-> Medecine,
        // Medecine <-> Reeducation,
        // ZoneBloquee est un obstacle
        urgences.addNeighbor(chirurgie);
        chirurgie.addNeighbor(urgences);
        chirurgie.addNeighbor(medecine);
        medecine.addNeighbor(chirurgie);
        medecine.addNeighbor(rehab);
        rehab.addNeighbor(medecine);
        // blocage n'a pas de voisins ou bien, on fait un blocage total

        // --- 3) Préparer la liste globale ---
        List<HospitalUnit> units = new ArrayList<>();
        units.add(urgences);
        units.add(chirurgie);
        units.add(medecine);
        units.add(rehab);
        units.add(blocage);
        return units;
    }

    /**
     * Fonction pour transférer des patients d'un service à un autre
     */
    private static void transfererPatients(String source, String destination, double maxTransfert) {
        double patientsTransf = Math.min(charges.get(source), maxTransfert);
        charges.put(source, charges.get(source) - patientsTransf);
        charges.put(destination, charges.get(destination) + patientsTransf);
        log("Transfert", source + " vers " + destination, patientsTransf);
    }

    /**
     * Affichage détaillé des charges actuelles
     */
    private static void afficherCharges() {
        for (String service : SERVICES) {
            System.out.printf("    %s: load=%.2f\n", service, charges.get(service));
        }
    }

    /**
     * Génère des logs détaillés
     */
    private static void log(String action, String service, double nombre) {
        if (nombre > 0) {
            System.out.printf("[LOG] %s : %.2f patients %s\n", action, nombre, service);
        }
    }
}
