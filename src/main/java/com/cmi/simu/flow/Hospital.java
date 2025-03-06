package com.cmi.simu.flow;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un hôpital, identifié par un ID et un nom.
 * Il contient :
 *   - une liste de HospitalUnit (services)
 *   - un FlowManager et FlowSimulator pour gérer l'écoulement interne
 *   - des voisins (autres hôpitaux) pour transférer des patients en cas de saturation
 */
@Getter
public class Hospital {

    @Getter
    private final int id;               // Identifiant unique (ex. pour requête POST)
    private final String name;           // Nom de l'hôpital

    private final List<HospitalUnit> units;   // Services internes
    private final List<Hospital> neighbors;   // Autres hôpitaux connectés

    // Pour la simulation interne
    @Setter
    private FlowManager flowManager;
    private FlowSimulator flowSimulator;

    /**
     * Constructeur
     */
    public Hospital(int id, String name) {
        this.id = id;
        this.name = name;
        this.units = new ArrayList<>();
        this.neighbors = new ArrayList<>();
        // Le flowManager et flowSimulator seront configurés plus tard
    }

    /**
     * Ajoute un service hospitalier (HospitalUnit) à cet hôpital.
     */
    public void addUnit(HospitalUnit unit) {
        this.units.add(unit);
    }

    /**
     * Initialise/Met à jour le FlowSimulator pour cet hôpital
     * en utilisant le flowManager courant.
     */
    public void initFlowSimulator() {
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
    public void simulateOneStep(ArrivalScenario scenario) {
        // 1) Gérer les arrivées extérieures (ex. scenario.updateArrivalsForHospital(this))
        scenario.updateArrivalsForHospital(this);

        // 2) Simulation interne (flux local)
        if (flowSimulator != null) {
            flowSimulator.simulateOneStep();
        }

        // 3) Tentative de débordement / transfert inter-hôpitaux
        doInterHospitalTransfers();
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
                        unit.transferSomePatients(neighborUnit, toTransfer);
                        surplus -= toTransfer;
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

}
