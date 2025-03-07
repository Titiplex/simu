package com.cmi.simu.flow;

import java.util.Arrays;
import java.util.List;

public class MainFlow {

    public static void main(String[] args) {

        int totalSteps = 20;

        List<Hospital> hospitalGraph = buildHospitalNetwork();
        ArrivalScenario scenario = new ArrivalScenario(hospitalGraph);

        for (int t = 0; t < totalSteps; t++) {

            System.out.println("*************************************************");
            // Affiche un petit résumé
            System.out.println("=== Time " + t + " ===");

            for (Hospital hospital : hospitalGraph) {
                System.out.println("Sorties : " + hospital.simulateOneTick(scenario));
            }

            // Logging
            logHospitalState(hospitalGraph);

            System.out.println("*************************************************");

            Clock.addOneHour();
        }

        System.out.println("Simulation terminee !");
    }

    private static void logHospitalState(List<Hospital> hospitalGraph) {
        for (Hospital hospital : hospitalGraph) {
            System.out.println("Hospital " + hospital.getName() + " (ID=" + hospital.getId() + ") :");
            int total = 0;
            for (HospitalUnit unit : hospital.getUnits()) {
                int load = unit.getPatients().size();
                total += load;
                System.out.println("  - " + unit.getName() + ": " + load + " patients");
            }
            System.out.println("  -> TOTAL in " + hospital.getName() + " = " + total);
        }
    }

    public static List<Hospital> buildHospitalNetwork() {
        // FlowManager standard pour tout le monde
        FlowManager fmA = new FlowManager(22.0, 4.4, 3.0);
        FlowManager fmB = new FlowManager(1.0, 0.3, 2.0);

        // 1) Crée deux hôpitaux
        Hospital hospitalA = new Hospital(1, "A", fmA);  // id=1
        Hospital hospitalB = new Hospital(2, "B", fmB);  // id=2

        // 2) Les relier en voisins
        hospitalA.addNeighbor(hospitalB);
        hospitalB.addNeighbor(hospitalA);

        return Arrays.asList(hospitalA, hospitalB);
    }
}
