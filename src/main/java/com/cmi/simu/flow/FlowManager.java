package com.cmi.simu.flow;

import lombok.Getter;
import lombok.Setter;

/**
 * Gère le calcul du flux F_{i → j} entre deux unités voisines,
 * en s'appuyant sur les principes d'écoulement (gravité, diffusion...).
 */
@Setter
@Getter
public class FlowManager {

    // ----- GET/SET -----
    // Coefficient global d'écoulement par gravité
    private double flowCoefficient;     // k
    // Coefficient (optionnel) pour la diffusion latérale (plus lente)
    private double lateralCoefficient;  // k_lat
    // Seuil de diffusion latérale (epsilon)
    private double lateralThreshold;    // epsilon

    /**
     * Constructeur
     */
    public FlowManager(double flowCoefficient, double lateralCoefficient, double lateralThreshold) {
        this.flowCoefficient = flowCoefficient;
        this.lateralCoefficient = lateralCoefficient;
        this.lateralThreshold = lateralThreshold;
    }

    /**
     * Calcule le flux de i → j selon l'équation :
     *   F_{i→j} = k * max(0, T_i - T_j)
     * en tenant compte d'un éventuel petit seuil lateralThreshold
     * pour la diffusion latérale.
     * <p>
     * Si T_i > T_j + epsilon → flux "gravitaire" (rapide).
     * Sinon, T_i > T_j → flux "latéral" (plus lent).
     * <p>
     * Retourne 0 si i est un obstacle ou si la charge i est nulle.
     */
    public double computeFlux(HospitalUnit i, HospitalUnit j) {
        if (i.isObstacle() || j.isObstacle()) {
            return 0.0;
        }
        double heightDiff = i.getTotalHeight() - j.getTotalHeight();
        if (heightDiff <= 0.0) {
            return 0.0; // pas de flux s'il n'y a pas de surplus
        }

        // Cas 1 : gravité
        if (heightDiff > lateralThreshold) {
            // F_{i->j} = k * (T_i - T_j)
            return flowCoefficient * heightDiff;
        }
        // Cas 2 : diffusion latérale (si T_i > T_j mais < T_j + epsilon)
        else {
            return lateralCoefficient * heightDiff;
        }
    }

}
