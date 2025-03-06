package com.cmi.simu.flow;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainFlow {

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
        int totalSteps = 20;
        for (int t = 0; t < totalSteps; t++) {
            // Met à jour externalArrivals dans chaque unité
            // selon le scénario (ex. t=10 → afflux massif)
            scenario.updateArrivals(t);

            // Lance un pas de simulation
            simulator.simulateOneStep();

            // Affiche un petit résumé
            System.out.println("=== Time " + t + " ===");
            int totalPatients = 0;
            for (HospitalUnit u : units) {
                int load = u.getCurrentLoad();
                totalPatients += load;
                System.out.printf("    %s: load=%.2f\n", u.getName(), (double) load);
            }
            System.out.println("   -> Total Patient = " + totalPatients);
        }

        System.out.println("Simulation terminee !");
    }

    @NotNull
    private static List<HospitalUnit> getHospitalUnits() {
        HospitalUnit urgences   = new HospitalUnit("Urgences",     15.0, 0.05, 50.0, false);
        HospitalUnit chirurgie  = new HospitalUnit("Chirurgie",    10.0, 0.02, 30.0, false);
        HospitalUnit medecine   = new HospitalUnit("Medecine",      9.0, 0.01, 40.0, false);
        HospitalUnit rehab      = new HospitalUnit("Reeducation",   5.0, 0.01, 20.0, false);
        HospitalUnit blocage    = new HospitalUnit("ZoneBloquee",   0.0, 0.00,  0.0, true);

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
}
