package com.cmi.simu.flow;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une unité hospitalière modélisée comme une "cellule"
 * dans l'analogie de l'écoulement. Chaque unité a – * altitude (H_i) *. Charge courante (W_i) *. Taux d'absorption (alpha_i) * . Statut "obstacle" ou non
 *   — liste de voisins pour calculer les flux
 */
@Getter
public class HospitalUnit {

    private final String name;            // Nom du service (ex. "Urgences", "Chirurgie", etc.)
    private final double altitude;        // H_i : hauteur de base (influence la hauteur totale)
    private int currentLoad;           // W_i(t) : charge à l'instant t
    private final double absorptionRate;  // alpha_i : fraction absorbée chaque pas de temps
    /**
     * — GETTER —
     *  Contrôle ou non le statut d'obstacle.
     */
    @Setter
    private boolean obstacle;             // true si l'unité bloque le flux
    @Setter
    private double maxCapacity;           // Pour modéliser une capacité théorique du service

    /**
     * — GETTER —
     *  Retourne la liste des voisins (adjacents) de cette unité.
     */
    // Liste des unités voisines (pour calculer flux entre elles)
    private final List<HospitalUnit> neighbors;

    /**
     * — GETTER —
     *  Nombre de patients arrivant de l'extérieur ce pas de temps
     *  (mis à jour par la logique d'événement).
     */
    // Pour gérer des arrivées extérieures variables
    // (On peut utiliser une fonction, un objet ou un simple paramètre qui évolue)
    @Setter
    private double externalArrivals;

    /**
     * Constructeur de base
     */
    public HospitalUnit(String name, double altitude, double absorptionRate, double maxCapacity, boolean isObstacle) {
        this.name = name;
        this.altitude = altitude;
        this.absorptionRate = absorptionRate;
        this.maxCapacity = maxCapacity;
        this.obstacle = isObstacle;
        this.currentLoad = 0;
        this.neighbors = new ArrayList<>();
        this.externalArrivals = 0.0;
    }

    /**
     * Calcule la "hauteur totale" T_i(t) = H_i + W_i(t).
     * @return la hauteur totale
     */
    public double getTotalHeight() {
        return altitude + currentLoad;
    }

    /**
     * Met à jour la charge en tenant compte des départs naturels
     * (ou absorption, alpha_i).
     * <p>
     * Modélise la formule :
     *   W_i(t+1) = W_i(t+1) * (1 - alpha_i)
     * si alpha_i > 0.
     */
    public void applyAbsorption() {
        double absorbed = currentLoad * absorptionRate;
        currentLoad -= (int) absorbed;
        // Variante : currentLoad = currentLoad * (1 - absorptionRate);
    }

    /**
     * Ajuste la charge en ajoutant un certain nombre de patients.
     */
    public void addPatients(double number) {
        currentLoad += (int) number;
        if (currentLoad > maxCapacity) {
            // On peut imaginer un seuil ou un comportement quand
            // la capacité est dépassée (ex : saturé).
            // Pour le moment, on se contente d'autoriser le dépassement,
            // ou bien, on force currentLoad = maxCapacity.
        }
    }

    /**
     * Retire un certain nombre de patients (par ex, si on en envoie vers d'autres services).
     */
    public void removePatients(int number) {
        currentLoad -= number;
        if (currentLoad < 0) {
            currentLoad = 0;
        }
    }

    /**
     * Ajoute un voisin à la liste de ceux qui reçoivent/envoyent du flux.
     */
    public void addNeighbor(HospitalUnit neighbor) {
        this.neighbors.add(neighbor);
    }

    // ----- GET / SET -----

}
