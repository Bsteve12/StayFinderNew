package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.outputDTO.OwnerMetricsResponseDTO;
import com.stayFinder.proyectoFinal.services.OwnerMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metrics/owner")
@RequiredArgsConstructor
@CrossOrigin(origins = "${frontend.url}")
@Tag(name = "Métricas Owner", description = "Endpoints para KPIs y estadísticas de Anfitriones")
public class OwnerMetricsController {

    private final OwnerMetricsService metricsService;

    @GetMapping("/{ownerId}/dashboard")
    @Operation(summary = "Obtener todas las métricas para el dashboard del owner (Alojamientos VS Reservas y gráficos)")
    public ResponseEntity<OwnerMetricsResponseDTO> getDashboardMetrics(@PathVariable Long ownerId) {
        OwnerMetricsResponseDTO stats = metricsService.getOwnerMetrics(ownerId);
        return ResponseEntity.ok(stats);
    }
}
