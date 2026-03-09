package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.AlojamientoRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.BloqueoDisponibilidadRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.AlojamientoResponseDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.BloqueoDisponibilidadResponseDTO;
import com.stayFinder.proyectoFinal.entity.enums.EstadoAlojamiento;
import com.stayFinder.proyectoFinal.services.alojamientoService.interfaces.AlojamientoServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Alojamientos", description = "Operaciones para gestionar alojamientos")
@RestController
@RequestMapping("/api/alojamientos")
@RequiredArgsConstructor
@CrossOrigin(origins = "${frontend.url}")
public class AlojamientoController {

    private final AlojamientoServiceInterface alojamientoService;

    @PostMapping
    @Operation(summary = "Crear un alojamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alojamiento creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<AlojamientoResponseDTO> crear(
            @jakarta.validation.Valid @RequestBody AlojamientoRequestDTO req,
            @Parameter(description = "ID del propietario del alojamiento") @RequestParam Long ownerId) {
        return ResponseEntity.ok(alojamientoService.crear(req, ownerId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar un alojamiento")
    public ResponseEntity<AlojamientoResponseDTO> editar(
            @Parameter(description = "ID del alojamiento a editar") @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody AlojamientoRequestDTO req,
            @Parameter(description = "ID del propietario del alojamiento") @RequestParam Long ownerId) {
        return ResponseEntity.ok(alojamientoService.editar(id, req, ownerId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un alojamiento")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del alojamiento a eliminar") @PathVariable Long id,
            @RequestParam Long ownerId) {
        alojamientoService.eliminar(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener alojamiento por ID")
    public ResponseEntity<AlojamientoResponseDTO> obtener(
            @Parameter(description = "ID del alojamiento a obtener") @PathVariable Long id) {
        return ResponseEntity.ok(alojamientoService.obtenerPorId(id));
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Obtener alojamientos de un owner específico")
    public ResponseEntity<List<AlojamientoResponseDTO>> obtenerPorOwner(
            @Parameter(description = "ID del owner a consultar") @PathVariable Long ownerId) {
        return ResponseEntity.ok(alojamientoService.obtenerAlojamientosDeOwner(ownerId));
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar todos los alojamientos activos")
    public ResponseEntity<List<AlojamientoResponseDTO>> obtenerAlojamientosActivos() {
        List<AlojamientoResponseDTO> activos = alojamientoService.listarAlojamientosActivos();
        return ResponseEntity.ok(activos);
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de un alojamiento")
    public ResponseEntity<AlojamientoResponseDTO> cambiarEstado(
            @Parameter(description = "ID del alojamiento") @PathVariable Long id,
            @Parameter(description = "Nuevo estado") @RequestParam EstadoAlojamiento estado,
            @Parameter(description = "ID del owner o admin") @RequestParam Long ownerId) {
        return ResponseEntity.ok(alojamientoService.cambiarEstado(id, estado, ownerId));
    }

    @PostMapping("/{id}/bloqueos")
    @Operation(summary = "Registrar un bloqueo de fechas para un alojamiento")
    public ResponseEntity<BloqueoDisponibilidadResponseDTO> bloquearFechas(
            @PathVariable Long id,
            @RequestBody BloqueoDisponibilidadRequestDTO req,
            @RequestParam Long ownerId) {
        return ResponseEntity.ok(alojamientoService.bloquearFechas(id, req, ownerId));
    }

    @GetMapping("/{id}/bloqueos")
    @Operation(summary = "Obtener bloqueos de fechas de un alojamiento")
    public ResponseEntity<List<BloqueoDisponibilidadResponseDTO>> obtenerBloqueos(
            @PathVariable Long id) {
        return ResponseEntity.ok(alojamientoService.obtenerBloqueos(id));
    }

    @GetMapping("/dashboard-stats")
    @Operation(summary = "Obtener estadísticas básicas de alojamientos para el Dashboard")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        return ResponseEntity.ok(alojamientoService.obtenerEstadisticasDashboard());
    }
}
