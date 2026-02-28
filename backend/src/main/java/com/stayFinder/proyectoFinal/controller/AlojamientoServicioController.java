package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.AlojamientoServicioRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.AlojamientoServicioResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alojamiento-servicios")
public class AlojamientoServicioController {

    @GetMapping
    @Operation(summary = "Listar relaciones alojamiento-servicio", description = "Obtiene todos los servicios asociados a los alojamientos")
    @ApiResponse(responseCode = "200", description = "Listado de relaciones")
    public List<AlojamientoServicioResponseDTO> listarRelaciones() {
        return List.of(
                new AlojamientoServicioResponseDTO(1L, 1L, "WiFi"),
                new AlojamientoServicioResponseDTO(1L, 2L, "Piscina")
        );
    }

    @PostMapping
    @Operation(summary = "Asignar un servicio a un alojamiento")
    @ApiResponse(responseCode = "201", description = "Relación creada")
    public AlojamientoServicioResponseDTO crearRelacion(@RequestBody AlojamientoServicioRequestDTO dto) {
        return new AlojamientoServicioResponseDTO(dto.getAlojamientoId(), dto.getServicioId(), "Ejemplo Servicio");
    }
}
