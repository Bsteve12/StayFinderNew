package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.HistorialReservasRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ReservaHistorialResponseDTO;
import com.stayFinder.proyectoFinal.entity.enums.EstadoReserva;
import com.stayFinder.proyectoFinal.services.reservaService.interfaces.ReservaServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
public class HistorialReservaController {

    private final ReservaServiceInterface reservaService;

    /**
     * Endpoint para historial de reservas de un usuario
     * GET /api/historial/usuario/{usuarioId}
     * Parámetros opcionales en query: fechaInicio, fechaFin, estado
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaHistorialResponseDTO>> historialUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String estado
    ) throws Exception {

        HistorialReservasRequestDTO filtros = new HistorialReservasRequestDTO(
                fechaInicio != null ? LocalDate.parse(fechaInicio) : null,
                fechaFin != null ? LocalDate.parse(fechaFin) : null,
                estado != null ? EstadoReserva.valueOf(estado.toUpperCase()) : null
        );

        return ResponseEntity.ok(reservaService.historialReservasUsuario(usuarioId, filtros));
    }

    /**
     * Endpoint para historial de reservas de un anfitrión
     * GET /api/historial/anfitrion/{ownerId}
     * Parámetros opcionales en query: fechaInicio, fechaFin, estado
     */
    @GetMapping("/anfitrion/{ownerId}")
    public ResponseEntity<List<ReservaHistorialResponseDTO>> historialAnfitrion(
            @PathVariable Long ownerId,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String estado
    ) throws Exception {

        HistorialReservasRequestDTO filtros = new HistorialReservasRequestDTO(
                fechaInicio != null ? LocalDate.parse(fechaInicio) : null,
                fechaFin != null ? LocalDate.parse(fechaFin) : null,
                estado != null ? EstadoReserva.valueOf(estado.toUpperCase()) : null
        );

        return ResponseEntity.ok(reservaService.historialReservasAnfitrion(ownerId, filtros));
    }
}
