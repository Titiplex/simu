package com.cmi.simu.flow;

import lombok.Getter;

/**
 * Représente un patient, avec – * une priorité
 *  — un temps de traitement restant (timeToTreat) *. Un temps déjà passé dans le service courant (timeSpentInService)
 */
@Getter
public class Patient {
    private final PriorityLevel priority;
    private int timeToTreat;         // nombre d'itérations nécessaires
    private int timeSpentInService;  // combien de steps déjà passés dans le service

    public Patient(PriorityLevel priority, int timeToTreat) {
        this.priority = priority;
        this.timeToTreat = timeToTreat;
        this.timeSpentInService = 0;
    }

    public void decreaseTimeToTreat() {
        if (timeToTreat > 0) timeToTreat--;
    }

    public void incrementTimeInService() {
        timeSpentInService++;
    }
}

