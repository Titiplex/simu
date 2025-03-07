package routes.controller;


import com.cmi.simu.flow.Hospital;
import service.HospitalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals") // URL de base
@CrossOrigin(origins = "http://localhost:5173") // Autorise Vue.js en dev

public class HospitalController {




    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping
    public List<Hospital> getAllHospitals() {
        return hospitalService.getAllHospitals();
    }

    // Route pour récupérer les services d'un hôpital spécifique avec capacité
    @GetMapping("/{id}/services")
    public List<Map<String, Object>> getHospitalServices(@PathVariable Long id) {
        return hospitalService.getHospitalServices(id);
    }

    @PostMapping
    public Hospital createHospital(@RequestBody Hospital hospital) {
        return hospitalService.createHospital(hospital);
    }

    @DeleteMapping("/{id}/")
    public String deleteHospital(@PathVariable Long id) {
        boolean deleted = hospitalService.deleteHospital(id);
        return deleted ? "Hôpital supprimé" : "Hôpital non trouvé";
    }

    @PostMapping("/{id}/services")
    public void addMaxCapacity(@PathVariable Long id,
                               @RequestBody Map<String, Object> UnitUpdateData){
        // Récupérer les données envoyées
        String unitName = (String) UnitUpdateData.get("serviceName");
        double newMaxCapacity = (Double) UnitUpdateData.get("maxCapacity");

        hospitalService.updateMaxCapacityUnit(id,unitName,newMaxCapacity);
    }
}
