package com.cmi.simu.routes.service;

import com.cmi.simu.flow.HospitalUnit;
import com.cmi.simu.flow.Hospital;
import com.cmi.simu.flow.ArrivalScenario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HospitalService {
    private final List<Hospital> hospitals = new ArrayList<>();
    private Long nextId = 1L;

    public HospitalService() {
    }

    public List<Hospital> getAllHospitals() {
        return hospitals;}

    public List<Map<String, Object>> getHospitalsWithServices() {
            for(Hospital h : hospitals) {
                h.simulateOneTick(new ArrivalScenario(hospitals))
            }
                return hospitalService.getAllHospitals().stream()
                    .map(hospital -> Map.of(
                            "id", hospital.getId(),
                            "name", hospital.getName(),
                            "services", hospital.getServices().stream()
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
            e.printStackTrace();
        }
        return map;
    }




    // Modification pour retourner les services avec la capacité maximale et occupée
    public List<Map<String, Object>> getHospitalServices(Long id) {
        // Recherche de l'hôpital par ID
        Hospital hospital = hospitals.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst()
                .orElse(null);

        // Si l'hôpital existe, transformer les services en Map avec capacité
        if (hospital != null) {
            return hospital.getUnits().stream()
                    .map(unit -> Map.of(
                            "name", unit.getName(),
                            "maxCapacity", unit.getMaxCapacity(),
                            "occupiedCapacity", unit.getCurrentLoad()
                    ))
                    .collect(Collectors.toList());
        }
        return null; // Retourner null si l'hôpital n'est pas trouvé
    }

    public Hospital createHospital(Hospital hospital) {
        hospital.setId(nextId++);
        hospitals.add(hospital);
        for(Hospital h : hospitals) {
            h.addNeighbor(hospital);
            hospital.addNeighbor(h);
        }
        return hospital;
    }

    public boolean deleteHospital(Long id) {
        return hospitals.removeIf(h -> h.getId().equals(id));
    }

    public void updateMaxCapacityUnit(long id, String unitName, double newMaxCapacity) {
        Hospital hospital = hospitals.stream()
                .filter(h -> h.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (hospital != null) {
            // Recherche du unit par nom dans l'hôpital
            HospitalUnit hospitalUnit = hospital.getunits().stream()
                    .filter(s -> s.getName().equalsIgnoreCase(serviceName))
                    .findFirst()
                    .orElse(null);

            if (hospitalUnit != null) {
                // Mise à jour de la capacité maximale du unit
                hospitalUnit.setMaxCapacity(newMaxCapacity);
                return true; // La mise à jour a réussi
            }
        }
    }
}
