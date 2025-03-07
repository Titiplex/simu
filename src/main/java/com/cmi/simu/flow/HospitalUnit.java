package com.cmi.simu.flow;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Représente une unité hospitalière modélisée comme une "cellule"
 * dans l'analogie de l'écoulement. Chaque unité a – * altitude (H_i) *. Charge courante (W_i) *. Taux d'absorption (alpha_i) * . Statut "obstacle" ou non
 * — liste de voisins pour calculer les flux
 */
@Getter
public class HospitalUnit {

    @Getter
    private final String name;            // Nom du service (ex. "Urgences", "Chirurgie", etc.)
    @Getter
    private final double altitude;        // H_i : hauteur de base (influence la hauteur totale)
    private int currentLoad;           // W_i(t) : charge à l'instant t
    @Setter
    @Getter
    private double absorptionRate;  // alpha_i : fraction absorbée chaque pas de temps
    @Setter
    @Getter
    private double mortalityRate = 0.02;  // 2% de chance de mourir chaque fois qu'on soigne un patient
    // Capacité du personnel : nbre max de patients traités par itération
    @Setter
    @Getter
    private int staffCapacity;

    // ----- Gestion des Patients -----
    // Liste de patients actuellement dans l'unité
    @Getter
    private final List<Patient> patients;

    // Pour modéliser l'arrivée d'un certain nb de patients extérieurs
    @Getter
    @Setter
    private int externalArrivalsUrgent;
    @Getter
    @Setter
    private int externalArrivalsNormal;
    @Getter
    @Setter
    private int externalArrivalsLow;
    /**
     * — GETTER —
     * Contrôle ou non le statut d'obstacle.
     */
    @Getter
    @Setter
    private boolean obstacle;             // true si l'unité bloque le flux
    @Getter
    @Setter
    private int maxCapacity;           // Pour modéliser une capacité théorique du service

    // ----- GET / SET -----
    /**
     * — GETTER —
     * Retourne la liste des voisins (adjacents) de cette unité.
     */
    // Liste des unités voisines (pour calculer flux entre elles)
    @Getter
    private final List<HospitalUnit> neighbors;

    /**
     * — GETTER —
     * Nombre de patients arrivant de l'extérieur ce pas de temps
     * (mis à jour par la logique d'événement).
     */
    // Pour gérer des arrivées extérieures variables
    // (On peut utiliser une fonction, un objet ou un simple paramètre qui évolue)
    @Setter
    private double externalArrivals;

    /**
     * Constructeur de base
     */
    public HospitalUnit(String name, int altitude, boolean isObstacle,
                        int staffCapacity, int maxCapacity, double absorptionRate) {
        this.name = name;
        this.altitude = altitude;
        this.obstacle = isObstacle;
        this.staffCapacity = staffCapacity;
        this.maxCapacity = maxCapacity;
        this.absorptionRate = absorptionRate;

        this.patients = new ArrayList<>();
        this.neighbors = new ArrayList<>();

        this.externalArrivalsUrgent = 0;
        this.externalArrivalsNormal = 0;
        this.externalArrivalsLow = 0;

        this.currentLoad = 0;
    }

    /**
     * Calcule la "hauteur totale" T_i(t) = H_i + W_i(t).
     *
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
     * W_i(t+1) = W_i(t+1) * (1 - alpha_i)
     * si alpha_i > 0.
     */
    public int applyAbsorption() {
        int absorbed = 0;

        if (absorptionRate <= 0) return 0;
        int total = patients.size();
        // Nombre de patients qui sortent (ex. guéris)
        int out = (int) Math.round(total * absorptionRate);
        if (out <= 0) return 0;

        // On retire d'abord les patients de plus faible priorité (par ex. ils sortent plus vite)
        // Ou au contraire, on retire aléatoirement. Ici, on retire aléatoirement pour simplifier :
        Random rand = new Random();
        for (int i = 0; i < out && !patients.isEmpty(); i++) {
            int idx = rand.nextInt(patients.size());
            patients.remove(idx);
            absorbed++;
        }
        // MAJ currentLoad
        currentLoad = patients.size();

        return absorbed;
    }

    /**
     * Ajoute plusieurs patients "génériques" (par ex. de priorité NORMAL).
     * Le paramètre 'number' représente le nombre de patients.
     * Chaque patient est créé avec un temps de traitement par défaut
     * (ici, on choisit NORMAL).
     */
    public void addPatients(int number) {
        // On arrondit le nombre à un entier

        for (int i = 0; i < number; i++) {
            // On crée un patient de priorité NORMAL par défaut
            Patient p = new Patient(PriorityLevel.NORMAL, randomTimeToTreat(PriorityLevel.NORMAL));

            // On essaie de l'ajouter
            boolean ok = addPatient(p);
            if (!ok) {
                // Service saturé : on peut imaginer un débordement...
                break;
            }
        }
    }

    /**
     * Retire un certain nombre de patients (au hasard par exemple),
     * puis met à jour currentLoad.
     */
    public void removePatients(int number) {
        if (number <= 0 || patients.isEmpty()) return;

        Random rand = new Random();
        for (int i = 0; i < number && !patients.isEmpty(); i++) {
            int idx = rand.nextInt(patients.size());
            patients.remove(idx);
        }
        currentLoad = patients.size();
    }

    /**
     * Ajoute un voisin à la liste de ceux qui reçoivent/envoyent du flux.
     */
    public void addNeighbor(HospitalUnit neighbor) {
        if (!this.neighbors.contains(neighbor)) {
            this.neighbors.add(neighbor);
        }
    }

    /**
     * Ajoute un patient P dans la liste, si la capacité n'est pas dépassée.
     * Met à jour currentLoad en conséquence.
     */
    public boolean addPatient(Patient p) {
        if (patients.size() < maxCapacity) {
            patients.add(p);
            // MAJ currentLoad
            currentLoad = patients.size();
            return true;
        } else {
            return false;
        }
    }

    public void removePatient(Patient p) {
        patients.remove(p);
        // MAJ currentLoad
        currentLoad = patients.size();
    }

    /**
     * On accepte les arrivées extérieures pour chaque priorité,
     * en créant effectivement les objets Patient correspondants.
     * Retourne le nombre de patients réellement acceptés (facultatif).
     */
    public int acceptExternalArrivals() {
        int acceptedCount = 0;

        // 1) Patients URGENT
        for (int i = 0; i < externalArrivalsUrgent; i++) {
            Patient p = new Patient(PriorityLevel.URGENT, randomTimeToTreat(PriorityLevel.URGENT));
            if (addPatient(p)) {
                acceptedCount++;
            } else {
                // Service saturé → on peut imaginer un "débordement" vers un autre service
                // Service saturé → on tente un débordement vers un voisin
                if (tryOverflowToNeighbors(p)) {
                    acceptedCount++;
                }
                // Si échec global, on peut imaginer un "patient perdu" ou en file d'attente globale
                // Pour l’exemple, on ne fait rien de plus
            }
        }

        // 2) Patients NORMAL
        acceptedCount = getAcceptedCount(acceptedCount, externalArrivalsNormal);

        // 3) Patients LOW
        for (int i = 0; i < externalArrivalsLow; i++) {
            Patient p = new Patient(PriorityLevel.LOW, randomTimeToTreat(PriorityLevel.LOW));
            if (addPatient(p)) {
                acceptedCount++;
            } else {
                if (tryOverflowToNeighbors(p)) {
                    acceptedCount++;
                }
                // Si échec global, on peut imaginer un "patient perdu" ou en file d'attente globale
                // Pour l’exemple, on ne fait rien de plus
            }
        }

        // 4) Si on souhaite aussi gérer externalArrivals "global" (sans priorité),
        //    on peut le faire ici :
        int genericCount = (int) Math.floor(externalArrivals);
        acceptedCount = getAcceptedCount(acceptedCount, genericCount);

        // Remettre à zéro pour la prochaine itération
        this.externalArrivalsUrgent = 0;
        this.externalArrivalsNormal = 0;
        this.externalArrivalsLow = 0;
        this.externalArrivals = 0.0;

        return acceptedCount;
    }

    private int getAcceptedCount(int acceptedCount, int genericCount) {
        for (int i = 0; i < genericCount; i++) {
            // Par exemple, on crée un patient NORMAL par défaut
            Patient p = new Patient(PriorityLevel.NORMAL, randomTimeToTreat(PriorityLevel.NORMAL));
            if (addPatient(p)) {
                acceptedCount++;
            } else {
                if (tryOverflowToNeighbors(p)) {
                    acceptedCount++;
                }
                // Si échec global, on peut imaginer un "patient perdu" ou en file d'attente globale
                // Pour l’exemple, on ne fait rien de plus
            }
        }
        return acceptedCount;
    }

    /**
     * Exemple de calcul du temps de traitement initial en fonction de la priorité
     */
    private int randomTimeToTreat(PriorityLevel priority) {
        // On peut affiner selon la pathologie...
        Random rand = new Random();
        return switch (priority) {
            case URGENT -> 3 + rand.nextInt(3);  // entre 3 et 5
            case NORMAL -> 5 + rand.nextInt(6);  // entre 5 et 10
            case LOW -> 8 + rand.nextInt(8);  // entre 8 et 15
        };
    }

    /**
     * Transfère "count" patients de ce service vers "targetUnit".
     * Par exemple, on enlève les patients de plus faible priorité
     * en premier, etc. On fait ici un tri simple : on retire aléatoirement.
     */
    public int transferSomePatients(HospitalUnit targetUnit, int count) {
        // Sélectionne "count" patients à retirer
        Random rand = new Random();
        int transferred = 0;
        for (int i = 0; i < count && !patients.isEmpty(); i++) {
            int idx = rand.nextInt(patients.size());
            Patient p = patients.get(idx);
            // On essaie d'ajouter p chez le voisin
            if (targetUnit.addPatient(p)) {
                // Retirer ici
                patients.remove(idx);
                transferred++;
            }
        }
        // MAJ
        currentLoad = patients.size();
        return transferred;
    }

    /**
     * Tente d'ajouter le patient P à l'un des voisins si le service actuel est saturé.
     * Retourne true si le patient a pu être placé chez un voisin, false sinon.
     */
    private boolean tryOverflowToNeighbors(Patient p) {
        // On pourrait faire un tri des voisins selon la hauteur, la distance, ou d'autres critères
        for (HospitalUnit neighbor : neighbors) {
            // Vérification basique : pas un obstacle, pas saturé
            if (!neighbor.isObstacle()) {
                if (neighbor.addPatient(p)) {
                    return true;
                }
            }
        }
        // Si aucun voisin n'a pu accueillir le patient
        return false;
    }

    /**
     * Méthode principale pour la logique "capacité du personnel" :
     * On traite (décrémente timeToTreat) jusqu'à 'staffCapacity' patients
     * lors de ce tick. Les autres ne sont pas soignés ce tour-ci.
     * <p>
     * On peut décider de prioriser l'URGENT, puis NORMAL, ensuite LOW.
     */
    public int treatPatientsOneStep() {
        if (patients.isEmpty() || staffCapacity <= 0) return 0;

        // 1) Tri par priorité (URGENT d'abord, puis NORMAL, ensuite LOW)
        //    → On veut soigner en priorité les urgences
        List<Patient> sorted = new ArrayList<>(patients);
        sorted.sort(Comparator.comparing(Patient::getPriority));
        // Rappel : par défaut, "URGENT" < "NORMAL" < "LOW" dans l'ordre alphabétique,
        //          donc on priorise URGENT d'abord.

        // 2) On traite (décrémente timeToTreat) jusqu'à staffCapacity patients
        int treatable = Math.min(staffCapacity, sorted.size());
        int effectivelyTreatedOrRemoved = 0;

        for (int i = 0; i < treatable; i++) {
            Patient p = sorted.get(i);

            // on traite la mortalité
            double r = Math.random();
            if (r <= mortalityRate) {
                p.setTimeToTreat(-999);
            } else {
                // On décrémente timeToTreat
                p.decreaseTimeToTreat();
                // On incrémente le temps passé
                p.incrementTimeInService();
            }

            effectivelyTreatedOrRemoved++;
        }

        // 3) Retirer du service ceux dont timeToTreat ← 0 (ils sortent du système)
        //    Il faut le faire depuis la liste 'patients' d'origine
        patients.removeIf(p -> p.getTimeToTreat() <= 0);

        // 4) MAJ currentLoad
        currentLoad = patients.size();

        return effectivelyTreatedOrRemoved;
    }
}
