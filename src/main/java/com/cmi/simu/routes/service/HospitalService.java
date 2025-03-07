package com.cmi.simu.routes.service;

import com.cmi.simu.flow.HospitalUnit;
import com.cmi.simu.flow.Hospital;
import com.cmi.simu.flow.ArrivalScenario;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HospitalService {
    private static final List<Hospital> hospitals = new ArrayList<>();
    private int nextId = 1;

    public HospitalService() {
    }

    public static List<Hospital> getAllHospitals() {
        return hospitals;}

    public List<Map<String, Object>> getHospitalsWithServices() {
            for(Hospital h : hospitals) {
                h.simulateOneTick(new ArrivalScenario(hospitals));
            }
                return HospitalService.getAllHospitals().stream()
                    .map(hospital -> Map.of(
                            "id", hospital.getId(),
                            "name", hospital.getName(),
                            "services", hospital.getUnits().stream()
                                    .map(this::convertObjectToMap)  // 🔥 Conversion automatique !
                                    .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList());
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
    public List<Map<String, Object>> getHospitalServices(Long id) {
        // Recherche de l'hôpital par ID
        Hospital hospital = hospitals.stream()
                .filter(h -> h.getId() == id)
                .findFirst()
                .orElse(null);

        // Si l'hôpital existe, transformer les services en Map avec capacité
        if (hospital != null) {
            return hospital.getUnits().stream()
                    .map(unit -> Map.<String, Object>of(
                            "name", unit.getName(),
                            "maxCapacity", unit.getMaxCapacity(),
                            "occupiedCapacity", unit.getCurrentLoad()
                    ))
                    .collect(Collectors.toList());
        }
        return null; // Retourner null si l'hôpital n'est pas trouvé
    }

    public Hospital createHospital() {
        Hospital hospital = new Hospital();
        hospital.setId(nextId++);
        for(Hospital h : hospitals) {
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


    public void deleteAllHospitals() {
        hospitals.clear();
    }
}
