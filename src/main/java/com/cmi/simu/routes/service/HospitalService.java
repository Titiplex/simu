package com.cmi.simu.routes.service;

import com.cmi.simu.flow.HospitalUnit;
import com.cmi.simu.flow.Hospital;
import com.cmi.simu.flow.ArrivalScenario;
import com.cmi.simu.routes.records.HospitalDTO;
import com.cmi.simu.routes.records.ServiceDTO;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HospitalService {
    @Getter
    private static final List<Hospital> hospitals = new ArrayList<>();
    private int nextId = 1;

    // -- SCENARIO PERSISTANT --
    // On le crée une seule fois,
    // et il s'incrémente (countdown, inEvent) au fur et à mesure des appels.
    private final ArrivalScenario scenario;

    public HospitalService() {
        scenario = new ArrivalScenario(hospitals);
    }

    public static List<Hospital> getAllHospitals() {
        return hospitals;
    }

    public List<HospitalDTO> getHospitalsWithServices() {

        for (Hospital h : hospitals) {
            h.simulateOneTick(scenario);
        }
        return hospitals.stream()
                .map(this::toHospitalDTO)
                .toList();
    }

    private Map<String, Object> convertObjectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);  // Permet d'accéder aux attributs privés
                map.put(field.getName(), field.get(obj));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return map;
    }


    // Modification pour retourner les services avec la capacité maximale et occupée
    public List<ServiceDTO> getHospitalServices(Long id) {
        // Recherche de l'hôpital par ID
        Hospital hospital = hospitals.stream()
                .filter(h -> h.getId() == id)
                .findFirst()
                .orElse(null);

        // Si l'hôpital existe, transformer les services en Map avec capacité
        if (hospital != null) {
            return hospital.getUnits().stream()
                    .map(u -> new ServiceDTO(u.getName(), u.getMaxCapacity(), u.getCurrentLoad()))
                    .toList();
        }
        return null; // Retourner null si l'hôpital n'est pas trouvé
    }

    public Hospital createHospital(Hospital hospital) {
        hospital.setId(nextId++);
        for (Hospital h : hospitals) {
            h.addNeighbor(hospital);
            hospital.addNeighbor(h);
        }
        hospitals.add(hospital);
        return hospital;
    }

    public boolean deleteHospital(Long id) {
        return hospitals.removeIf(h -> h.getId() == id);
    }

    public boolean updateMaxCapacityUnit(long id, String unitName, double newMaxCapacity) {
        Hospital hospital = hospitals.stream()
                .filter(h -> h.getId() == id)
                .findFirst()
                .orElse(null);
        if (hospital != null) {
            // Recherche du unit par nom dans l'hôpital
            HospitalUnit hospitalUnit = hospital.getUnits().stream()
                    .filter(s -> s.getName().equalsIgnoreCase(unitName))
                    .findFirst()
                    .orElse(null);

            if (hospitalUnit != null) {
                // Mise à jour de la capacité maximale du unit
                hospitalUnit.setMaxCapacity((int) newMaxCapacity);
                return true; // La mise à jour a réussi
            }
        }
        return false;
    }

<<<<<<< HEAD
    /**
     * Convertit un Hospital en DTO, en copiant seulement les champs utiles.
     * On exclut 'neighbors' pour éviter la récursion.
     */
    private HospitalDTO toHospitalDTO(Hospital h) {
        List<ServiceDTO> serviceDTOs = h.getUnits().stream()
                .map(u -> new ServiceDTO(u.getName(), u.getMaxCapacity(), u.getCurrentLoad()))
                .toList();
        return new HospitalDTO(h.getId(), h.getName(), serviceDTOs);
=======

    public void deleteAllHospitals() {
        hospitals.clear();
>>>>>>> e627eb251183d6234cf35d500bff0b6eab0cec71
    }
}
