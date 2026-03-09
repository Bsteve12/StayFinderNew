package com.stayFinder.proyectoFinal.dto.outputDTO;

import java.util.Map;

public record OwnerMetricsResponseDTO(
        Long totalAlojamientos,
        Long totalReservas,
        Map<String, Double> ingresosPorMes) {
}
