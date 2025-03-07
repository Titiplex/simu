package com.cmi.simu.routes.records;

/**
 * Représentation JSON d’un service/unité pour l’API.
 */
public record ServiceDTO(String name, int maxCapacity, int occupiedCapacity) {}
