package com.cmi.simu.flow;

import java.util.List;

/**
 * Gère la logique d'arrivées extérieures variables (ex. évènements).
 * <p>
 * Idée : on peut mettre à jour externalArrivals de chaque unité
 * selon un scénario (fichier, base de données, événement ponctuel, etc.)
 */
public class ArrivalScenario {

    private final List<Hospital> allHospitals;

    public ArrivalScenario(List<Hospital> hospitals) {
        this.allHospitals = hospitals;
    }

    /**
     * Exemple simple : à chaque itération (timeStep),
     * on peut injecter un certain nombre de patients
     * sur une unité précise (ex. Urgences) pour simuler
     * un afflux ponctuel.
     */
    public void updateArrivals(int timeStep) {

        for (Hospital hospital : allHospitals) {
            for (HospitalUnit unit : hospital.getUnits()) {
                // Par défaut, 0
                int urgentArr = 0;
                int normalArr = 0;
                int lowArr = 0;

                // Ex : si ce sont les "Urgences", gros afflux à t=10
                if (unit.getName().equalsIgnoreCase("Urgences")) {
                    if (timeStep == 10) {
                        urgentArr = 5;  // 5 patients URGENT
                        normalArr = 10; // 10 patients normaux
                        lowArr = 6;
                    } else if (timeStep < 10) {
                        urgentArr = 1;  // flux modéré
                        normalArr = 2;
                        lowArr = 3;
                    } else {
                        urgentArr = 2;
                        normalArr = 3;
                        lowArr = 3;
                    }
                }
//            else {
//                // Ex : flux stable ailleurs
//                normalArr = 1;
//                lowArr = 1;
//            }

                // On affecte
                unit.setExternalArrivalsUrgent(urgentArr);
                unit.setExternalArrivalsNormal(normalArr);
                unit.setExternalArrivalsLow(lowArr);
            }
        }
    }
}
