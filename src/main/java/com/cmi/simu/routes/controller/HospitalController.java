package com.cmi.simu.routes.controller;


import com.cmi.simu.flow.Clock;
import com.cmi.simu.flow.Hospital;
import com.cmi.simu.routes.service.HospitalService;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hospitals") // URL de base
@CrossOrigin(origins = "*") // Autorise Vue.js en dev

public class HospitalController {


    private static HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        HospitalController.hospitalService = hospitalService;
    }

    @GetMapping
    public static List<Map<String, Object>> getAllHospitals() {

        Clock.addOneHour();
        return hospitalService.getHospitalsWithServices();
    }


    // Route pour récupérer les services d'un hôpital spécifique avec capacité
    @GetMapping("/{id}/services")
    public List<Map<String, Object>> getHospitalServices(@PathVariable Long id) {
        return hospitalService.getHospitalServices(id);
    }


    @PostMapping
    public Hospital createHospital(@RequestBody Hospital hospital) {
        return hospitalService.createHospital();
    }

    @DeleteMapping("/{id}/")
    public String deleteHospital(@PathVariable Long id) {
        boolean deleted = hospitalService.deleteHospital(id);
        return deleted ? "Hôpital supprimé" : "Hôpital non trouvé";
    }

    @PostMapping("/{id}/services")
    public void addMaxCapacity(@PathVariable Long id,
                               @RequestBody Map<String, Object> UnitUpdateData) {
        // Récupérer les données envoyées
        String unitName = (String) UnitUpdateData.get("serviceName");
        double newMaxCapacity = (Double) UnitUpdateData.get("maxCapacity");

        hospitalService.updateMaxCapacityUnit(id, unitName, newMaxCapacity);
    }
}
