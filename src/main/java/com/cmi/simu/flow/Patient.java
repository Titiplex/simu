package com.cmi.simu.flow;

import lombok.Getter;
import lombok.Setter;

/**
 * Représente un patient, avec – * une priorité
 *  — un temps de traitement restant (timeToTreat) *. Un temps déjà passé dans le service courant (timeSpentInService)
 */
@Getter
public class Patient {
    private final PriorityLevel priority;

    @Setter
    private int timeToTreat;         // nombre d'itérations nécessaires

    private int timeSpentInService;  // combien de steps déjà passés dans le service

    @Getter
    // Nouveau : temps minimal avant transfert vers un autre service
    private int timeBeforeEligibleTransfer;

    @Getter
    // Nouveau : durée de séjour minimale avant d'être autorisé à sortir
    private final int minStayInUnit;

    public Patient(PriorityLevel priority, int timeToTreat) {
        this.priority = priority;
        this.timeToTreat = timeToTreat;
        this.timeSpentInService = 0;
        this.timeBeforeEligibleTransfer = 3;
        this.minStayInUnit = 2;
    }

    public void decreaseTimeToTreat() {
        if (timeToTreat > 0) timeToTreat--;
    }

    public void incrementTimeInService() {
        timeSpentInService++;
        if (timeBeforeEligibleTransfer > 0) {
            timeBeforeEligibleTransfer--;
        }
    }
}

