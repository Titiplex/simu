package com.cmi.simu.flow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coordonne la simulation pas à pas sur l'ensemble des unités.
 * <p>
 * 1) Calculer tous les flux F_{i→j} de manière simultanée
 *    (stockage dans une structure temporaire).
 * 2) Appliquer ces flux pour mettre à jour W_i(t+1).
 * 3) Appliquer l'absorption, gérer les arrivées extérieures, etc.
 */
public class FlowSimulator {

    private final List<HospitalUnit> units;
    private final FlowManager flowManager;

    // Pour éviter de mettre à jour "au fil de l'eau" et fausser le calcul
    // on stocke d'abord les flux dans cette map : (i->j) -> flux
    private final Map<String, Double> fluxMap;

    public FlowSimulator(List<HospitalUnit> units, FlowManager flowManager) {
        this.units = units;
        this.flowManager = flowManager;
        this.fluxMap = new HashMap<>();
    }

    /**
     * Exécute une itération (un pas de temps) :
     *   1) Réinitialise la fluxMap
     *   2) Calcule tous les flux sortants i->j
     *   3) Applique ces flux pour mettre à jour la charge
     *   4) Gère l'absorption, les arrivées extérieures, etc.
     */
    public void simulateOneStep() {
        fluxMap.clear();

        // 1) Calcul des flux i->j
        for (HospitalUnit i : units) {
            double availableLoad = i.getCurrentLoad();  // charge dispo avant écoulement
            if (availableLoad <= 0 || i.isObstacle()) continue;

            // On calcule combien on transfère vers chaque voisin
            for (HospitalUnit j : i.getNeighbors()) {
                if (j == i) continue; // sûreté
                double flux = flowManager.computeFlux(i, j);
                // On ne peut pas transférer plus que la charge disponible de i
                if (flux > availableLoad) {
                    flux = availableLoad;
                }
                if (flux > 0) {
                    String key = buildKey(i, j);
                    fluxMap.put(key, flux);
                    // On diminue la charge disponible au fur et à mesure
                    availableLoad -= flux;
                    if (availableLoad <= 0) break;
                }
            }
        }

        // 2) Mise à jour W_i(t+1) après flux
        //    On calcule d'abord le total sortant et entrant pour chaque unité
        Map<HospitalUnit, Double> totalOut = new HashMap<>();
        Map<HospitalUnit, Double> totalIn  = new HashMap<>();
        for (HospitalUnit u : units) {
            totalOut.put(u, 0.0);
            totalIn.put(u, 0.0);
        }

        for (Map.Entry<String, Double> entry : fluxMap.entrySet()) {
            String key = entry.getKey();
            double flux = entry.getValue();
            HospitalUnit i = parseSource(key);
            HospitalUnit j = parseTarget(key);

            totalOut.put(i, totalOut.get(i) + flux);
            totalIn.put(j, totalIn.get(j) + flux);
        }

        // Appliquer la mise à jour
        for (HospitalUnit u : units) {
            double oldLoad = u.getCurrentLoad();
            double newLoad = oldLoad - totalOut.get(u) + totalIn.get(u);
            // On applique ensuite les arrivées extérieures
            newLoad += u.getExternalArrivals();
            // On met à jour
            u.removePatients(oldLoad); // on retire la totalité "ancienne"
            u.addPatients(newLoad);    // on pose la nouvelle

            // Remettre les arrivées à 0 après usage (optionnel si on les met à jour à chaque itération)
            u.setExternalArrivals(0.0);
        }

        // 3) Appliquer l'absorption
        for (HospitalUnit u : units) {
            u.applyAbsorption();
        }
    }

    /**
     * Exécute la simulation sur plusieurs pas de temps
     */
    public void runSimulation(int steps) {
        for (int t = 0; t < steps; t++) {
            simulateOneStep();
        }
    }

    // --- Outils privés pour fluxMap ---

    private String buildKey(HospitalUnit i, HospitalUnit j) {
        return i.getName() + "->" + j.getName();
    }

    private HospitalUnit parseSource(String key) {
        // Avant la flèche "->"
        String[] parts = key.split("->");
        String sourceName = parts[0];
        return findUnitByName(sourceName);
    }

    private HospitalUnit parseTarget(String key) {
        // Après la flèche "->"
        String[] parts = key.split("->");
        String targetName = parts[1];
        return findUnitByName(targetName);
    }

    private HospitalUnit findUnitByName(String name) {
        for (HospitalUnit u : units) {
            if (u.getName().equals(name)) {
                return u;
            }
        }
        return null;
    }
}
