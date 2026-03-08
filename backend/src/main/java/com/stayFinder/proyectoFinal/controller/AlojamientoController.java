package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.AlojamientoRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.BloqueoDisponibilidadRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.AlojamientoResponseDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.BloqueoDisponibilidadResponseDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.DisponibilidadDTO;
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

@Tag(name = "Alojamientos", description = "Operaciones para gestionar alojamientos")
@RestController
@RequestMapping("/api/alojamientos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
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

    // --- Nuevos Endpoints de Gestión de Estado y Calendario ---

    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de un alojamiento", description = "Idealmente solo para ADMIN. Transiciona entre BORRADOR, ACTIVO, SUSPENDIDO.")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoAlojamiento estado,
            @RequestParam Long adminId) {
        alojamientoService.cambiarEstado(id, estado, adminId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/calendario")
    @Operation(summary = "Obtener el calendario de disponibilidad de un alojamiento", description = "Combina reservas confirmadas y bloqueos manuales.")
    public ResponseEntity<List<DisponibilidadDTO>> obtenerCalendario(@PathVariable Long id) {
        return ResponseEntity.ok(alojamientoService.obtenerCalendario(id));
    }

    @PostMapping("/{id}/bloqueos")
    @Operation(summary = "Agregar un bloqueo manual al calendario del alojamiento")
    public ResponseEntity<BloqueoDisponibilidadResponseDTO> agregarBloqueo(
            @PathVariable Long id,
            @RequestBody BloqueoDisponibilidadRequestDTO dto,
            @RequestParam Long ownerId) {
        BloqueoDisponibilidadRequestDTO request = new BloqueoDisponibilidadRequestDTO(
                id, dto.fechaInicio(), dto.fechaFin(), dto.motivo());
        return ResponseEntity.ok(alojamientoService.agregarBloqueo(request, ownerId));
    }

    @DeleteMapping("/bloqueos/{bloqueoId}")
    @Operation(summary = "Eliminar un bloqueo manual del calendario")
    public ResponseEntity<Void> eliminarBloqueo(
            @PathVariable Long bloqueoId,
            @RequestParam Long ownerId) {
        alojamientoService.eliminarBloqueo(bloqueoId, ownerId);
        return ResponseEntity.noContent().build();
    }
}
