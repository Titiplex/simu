package com.cmi.simu.flow;

import java.util.List;
import java.util.Random;

/**
 * Gère la logique d'arrivées extérieures aléatoires et ponctuellement massives (événements graves).
 *
 * - Pas de timeStep en paramètre. Chaque appel à updateArrivals() correspond à "une itération"
 *   ou "un certain instant" dans la simulation.
 * - L'enchaînement normal vs. événement est géré par un état interne (inEvent, countdownToNextEvent, eventDuration).
 */
public class ArrivalScenario {

    private final List<Hospital> allHospitals;
    private final Random rand;

    // -- État interne pour générer les événements aléatoires --
    private boolean inEvent;               // Sommes-nous dans un événement grave ?
    private int eventDuration;            // Combien "d'itérations" restantes pour l'événement
    private int countdownToNextEvent;     // Dans combien d'itérations arrive le prochain événement ?

    // Paramètres pour limiter la génération aléatoire
    // (on peut ajuster ces valeurs pour calibrer la fréquence et l'impact)
    private final int minTimeBetweenEvents = 7;  // minimum d'itérations entre deux événements
    private final int maxTimeBetweenEvents = 30;  // maximum d'itérations entre deux événements

    public ArrivalScenario(List<Hospital> hospitals) {
        this.allHospitals = hospitals;
        this.rand = new Random();

        this.inEvent = false;
        this.eventDuration = 0;
        // On déclenche d'abord un compte à rebours aléatoire avant le premier événement
        this.countdownToNextEvent = getRandomInRange(minTimeBetweenEvents, maxTimeBetweenEvents);
    }

    /**
     * Fonction principale qui génère les arrivées pour tous les hôpitaux
     * à chaque appel. Elle ne prend plus de timeStep, car on gère un flux
     * entièrement aléatoire (avec parfois des événements).
     */
    public void updateArrivals() {
        // 1) Vérifier si on est en événement
        if (inEvent) {
            // on décrémente la durée
            eventDuration--;
            if (eventDuration <= 0) {
                // fin de l'événement
                inEvent = false;
                // on recalcule un nouveau countdown avant le prochain événement
                countdownToNextEvent = getRandomInRange(minTimeBetweenEvents, maxTimeBetweenEvents);
            }
        } else {
            // pas d'événement → flux normal,
            // on décrémente le compte à rebours
            countdownToNextEvent--;
            if (countdownToNextEvent <= 0) {
                // on déclenche un nouvel événement
                inEvent = true;
                // durée max d'un événement
                int maxEventDuration = 3;
                // durée min d'un événement
                int minEventDuration = 1;
                eventDuration = getRandomInRange(minEventDuration, maxEventDuration);
            }
        }

        // 2) Générer des arrivées en fonction de l'État (inEvent ou non)
        for (Hospital hospital : allHospitals) {
            for (HospitalUnit unit : hospital.getUnits()) {

                // On calcule un triple (urgentArr, normalArr, lowArr) aléatoire
                // dépendant de si on est dans un événement ou non.
                int urgentArr = 0, normalArr = 0, lowArr = 0;

                if (inEvent) {
                    // Événement grave : flux plus élevé, plus de patients urgents
                    // Par exemple, urgences reçoivent un plus gros choc
                    // On peut affiner si unit.getName().equalsIgnoreCase("Urgences")

                    if (unit.getName().equalsIgnoreCase("Urgences")) {
                        // Événement + Urgences → gros afflux
                        urgentArr  = getRandomInRange(4, 8);
                        normalArr  = getRandomInRange(4, 11);
                        lowArr     = getRandomInRange(2, 6);
                    }
//                    else {
//                        // Événement, mais pas urgences → afflux un peu moindre
//                        urgentArr  = getRandomInRange(1, 4);
//                        normalArr  = getRandomInRange(3, 7);
//                        lowArr     = getRandomInRange(2, 5);
//                    }

                } else {
                    // Situation normale : flux plus modéré
                    if (unit.getName().equalsIgnoreCase("Urgences")) {
                        urgentArr  = getRandomInRange(0, 2);
                        normalArr  = getRandomInRange(2, 4);
                        lowArr     = getRandomInRange(1, 3);
                    }
//                    else {
//                        // services classiques
//                        urgentArr  = getRandomInRange(0, 1);
//                        normalArr  = getRandomInRange(1, 4);
//                        lowArr     = getRandomInRange(0, 2);
//                    }
                }

                // On affecte ces arrivées au service
                unit.setExternalArrivalsUrgent(urgentArr);
                unit.setExternalArrivalsNormal(normalArr);
                unit.setExternalArrivalsLow(lowArr);
            }
        }
    }

    // -- Outils privés --

    /**
     * Retourne un entier aléatoire compris entre min et max inclus
     */
    private int getRandomInRange(int min, int max) {
        if (min > max) {
            return min;
        }
        return rand.nextInt(max - min + 1) + min;
    }
}
