package com.cmi.simu.flow;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Représente un hôpital, identifié par un ID et un nom.
 * Il contient :
 *   - une liste de HospitalUnit (services)
 *   - un FlowManager et FlowSimulator pour gérer l'écoulement interne
 *   - des voisins (autres hôpitaux) pour transférer des patients en cas de saturation
 */
@Getter
public class Hospital {

    @Getter @Setter
    private int id;               // Identifiant unique (ex. pour requête POST)
    private final String name;           // Nom de l'hôpital

    private final List<HospitalUnit> units;   // Services internes
    private final List<Hospital> neighbors;   // Autres hôpitaux connectés

    // Pour la simulation interne
    private final FlowManager flowManager;
    private final FlowSimulator flowSimulator;

    /**
     * Constructeur
     */
    public Hospital(int id, String name, FlowManager flowManager) {
        this.id = id;
        this.name = name;
        this.units = getHospitalUnits();
        this.neighbors = new ArrayList<>();
        // Le flowManager et flowSimulator seront configurés plus tard
        this.flowManager = flowManager;
        this.flowSimulator = new FlowSimulator(units, flowManager);
    }

    /**
     * Ajoute un hôpital voisin (sur le graphe).
     */
    public void addNeighbor(Hospital other) {
        if (!this.neighbors.contains(other)) {
            this.neighbors.add(other);
        }
    }

    /**
     * Simule un "tick" (un pas de temps) à l'intérieur de l'hôpital,
     * puis tente d'éventuels transferts de patients vers les hôpitaux voisins.
     * On peut passer un scénario qui gère les arrivées pour cet hôpital.
     */
    public Map<String, Integer> simulateOneTick(ArrivalScenario scenario) {

        Map<String, Integer> sortis = new HashMap<>();
        // Simulation interne (flux local)
        if (flowSimulator != null) {
            sortis = flowSimulator.runOneStep(scenario, this.getUnits());
        }

        // 3) Tentative de débordement / transfert inter-hôpitaux
        doInterHospitalTransfers();

        return sortis;
    }

    /**
     * Tentative de transférer des patients vers des hôpitaux voisins
     * si un service est saturé. Ex : le même "serviceName" existe chez le voisin,
     * on envoie le surplus si le voisin a encore de la place.
     */
    private void doInterHospitalTransfers() {
        for (HospitalUnit unit : units) {
            // Regarde si saturé
            if (unit.getPatients().size() > unit.getMaxCapacity()) {
                // Surplus
                int surplus = unit.getPatients().size() - unit.getMaxCapacity();

                // Tenter de transférer 'surplus' patients vers un hôpital voisin
                // dans le même type de service (même name).
                for (Hospital neighborHospital : neighbors) {
                    HospitalUnit neighborUnit = neighborHospital.findUnitByName(unit.getName());
                    if (neighborUnit == null) continue;

                    // Combien le voisin peut-il accepter ?
                    int canAccept = neighborUnit.getMaxCapacity() - neighborUnit.getPatients().size();
                    if (canAccept > 0) {
                        int toTransfer = Math.min(surplus, canAccept);
                        // Transférer toTransfer patients (de plus basse priorité par ex.)
                        surplus -= unit.transferSomePatients(neighborUnit, toTransfer);
                        if (surplus <= 0) {
                            break; // plus de surplus
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrouve le service (HospitalUnit) par son nom (ex. "Urgences").
     * Retourne null si inexistant.
     */
    public HospitalUnit findUnitByName(String name) {
        for (HospitalUnit u : units) {
            if (u.getName().equalsIgnoreCase(name)) {
                return u;
            }
        }
        return null;
    }

    @NotNull
    private static List<HospitalUnit> getHospitalUnits() {
        HospitalUnit urgences  = new HospitalUnit("Urgences",  15, false, 10, 30, 0.0);
        HospitalUnit chirurgie = new HospitalUnit("Chirurgie", 10, false, 8,  20, 0.1);
        HospitalUnit medecine  = new HospitalUnit("Medecine",  8,  false, 5,  25, 0.05);
        HospitalUnit bloc    = new HospitalUnit("Bloque",    1,  false,  2,  1,  0.001); // obstacle

        // --- 2) Définir les voisinages (graphe) ---
        // Admettons :
        // Urgences <-> Chirurgie,
        // Chirurgie <-> Medecine,
        // Medecine <-> Reeducation,
        // ZoneBloquee est un obstacle
        urgences.addNeighbor(chirurgie);
        urgences.addNeighbor(medecine);
        chirurgie.addNeighbor(urgences);
        chirurgie.addNeighbor(medecine);
        medecine.addNeighbor(chirurgie);
        medecine.addNeighbor(urgences);
        chirurgie.addNeighbor(bloc);
        urgences.addNeighbor(bloc);
        bloc.addNeighbor(urgences);
        bloc.addNeighbor(chirurgie);
        // blocage n'a pas de voisins ou bien, on fait un blocage total

        // --- 3) Préparer la liste globale ---
        List<HospitalUnit> units = new ArrayList<>();
        units.add(urgences);
        units.add(chirurgie);
        units.add(medecine);
        units.add(bloc);
        return units;
    }
}
