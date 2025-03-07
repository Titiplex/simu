package com.cmi.simu.routes.records;

import java.util.List;

/**
 * Représentation JSON d’un hôpital pour l’API.
 */
public record HospitalDTO(int id, String name, List<ServiceDTO> services) {}
