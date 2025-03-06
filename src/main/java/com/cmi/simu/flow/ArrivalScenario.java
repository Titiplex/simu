package com.cmi.simu.flow;

import java.util.List;

/**
 * Gère la logique d'arrivées extérieures variables (ex. évènements).
 * <p>
 * Idée : on peut mettre à jour externalArrivals de chaque unité
 * selon un scénario (fichier, base de données, événement ponctuel, etc.)
 */
public class ArrivalScenario {

    private final List<HospitalUnit> units;

    public ArrivalScenario(List<HospitalUnit> units) {
        this.units = units;
    }

    /**
     * Exemple simple : à chaque itération (timeStep),
     * on peut injecter un certain nombre de patients
     * sur une unité précise (ex. Urgences) pour simuler
     * un afflux ponctuel.
     */
    public void updateArrivals(int timeStep) {
        // Réinitialiser ou calculer les arrivées pour chaque unité selon le timeStep
        // Ex. si t = 10, un accident cause un afflux massif de 20 patients aux Urgences
        for (HospitalUnit u : units) {
            // Par défaut, 0
            double arrivals = 0.0;

            // Cas : "Urgences"
            if (u.getName().equalsIgnoreCase("Urgences")) {
                if (timeStep == 10) {
                    arrivals = 20.0; // afflux massif
                } else if (timeStep < 10) {
                    arrivals = 3.0;  // afflux normal
                } else {
                    arrivals = 5.0;  // un peu plus tard
                }
            }

            // Cas : "Consultations"
            if (u.getName().equalsIgnoreCase("Consultations")) {
                arrivals = 2.0; // flux stable
            }

            // On peut étendre le scénario selon d'autres services...

            u.setExternalArrivals(arrivals);
        }
    }
}
