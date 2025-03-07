package com.cmi.simu.flow;

import java.util.List;
import java.util.Random;

/**
 * Gère la logique d'arrivées extérieures, incluant des événements (catastrophes) aléatoires.
 */
public class ArrivalScenario {

    private final List<Hospital> allHospitals;
    private final Random rand;

    // État interne
    private boolean inEvent;
    private int eventDuration;
    private int countdownToNextEvent;

    // Réduire pour voir des événements plus souvent :
    private final int minTimeBetweenEvents = 2;  // ex. 2
    private final int maxTimeBetweenEvents = 5;  // ex. 5

    public ArrivalScenario(List<Hospital> hospitals) {
        this.allHospitals = hospitals;
        this.rand = new Random();

        this.inEvent = false;
        // Durée initiale d’un événement inexistant
        this.eventDuration = 0;
        // On commence avec un compte à rebours aléatoire
        this.countdownToNextEvent = getRandomInRange(minTimeBetweenEvents, maxTimeBetweenEvents);
    }

    /**
     * Méthode principale, appelée à chaque "tick" (chaque fois qu'on veut avancer la simulation).
     */
    public void updateArrivals() {
        if (inEvent) {
            // On est déjà en événement
            eventDuration--;
            if (eventDuration <= 0) {
                inEvent = false;
                System.out.println("** L'evenement s'acheve. Activite normale. **");
                countdownToNextEvent = getRandomInRange(minTimeBetweenEvents, maxTimeBetweenEvents);
            }
        } else {
            // Pas d’événement => décompte avant le prochain
            countdownToNextEvent--;
            if (countdownToNextEvent <= 0) {
                inEvent = true;
                eventDuration = getRandomInRange(1, 3);
                System.out.println("** Un événement grave démarre pour " + eventDuration + " iteration(s) ! **");
            }
        }

        // Générer les arrivées dans chaque hôpital, en fonction de inEvent
        for (Hospital hospital : allHospitals) {
            for (HospitalUnit unit : hospital.getUnits()) {
                int urgentArr = 0, normalArr = 0, lowArr = 0;

                if (inEvent) {
                    // On augmente le flux, surtout en urgences
                    if (unit.getName().equalsIgnoreCase("Urgences")) {
                        System.out.println("Is Event for URGENCES");
                        urgentArr = getRandomInRange(3, 8);
                        normalArr = getRandomInRange(4, 10);
                        lowArr    = getRandomInRange(2, 5);
                    } else {
                        urgentArr = getRandomInRange(1, 3);
                        normalArr = getRandomInRange(1, 5);
                        lowArr    = getRandomInRange(0, 4);
                    }
                } else {
                    // Activité normale. On gère un mode jour/nuit par Clock
                    if (isNight()) {
                        if (unit.getName().equalsIgnoreCase("Urgences")) {
                            System.out.println("Night - URGENCES");
                            urgentArr = getRandomInRange(0, 2);
                            normalArr = getRandomInRange(0, 2);
                            lowArr    = getRandomInRange(0, 1);
                        } else {
                            urgentArr = getRandomInRange(0, 1);
                            normalArr = getRandomInRange(0, 2);
                            lowArr    = getRandomInRange(0, 1);
                        }
                    } else {
                        // Jour
                        if (unit.getName().equalsIgnoreCase("Urgences")) {
                            System.out.println("Day - URGENCES");
                            urgentArr = getRandomInRange(0, 2);
                            normalArr = getRandomInRange(2, 4);
                            lowArr    = getRandomInRange(1, 3);
                        } else {
                            urgentArr = getRandomInRange(0, 1);
                            normalArr = getRandomInRange(1, 3);
                            lowArr    = getRandomInRange(0, 2);
                        }
                    }
                }

                unit.setExternalArrivalsUrgent(urgentArr);
                unit.setExternalArrivalsNormal(normalArr);
                unit.setExternalArrivalsLow(lowArr);
            }
        }
    }

    private int getRandomInRange(int min, int max) {
        if (min > max) {
            return min;
        }
        return rand.nextInt(max - min + 1) + min;
    }

    private boolean isNight() {
        // Suppose qu'on est la nuit entre 22h et 5h
        int hour = Clock.getTime();
        return hour >= 22 || hour < 6;
    }
}
