package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.ActualizarReservaRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.CancelarReservaRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.CreateReservaRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ReservaHistorialResponseDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ReservaResponseDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.ReservaRequestDTO;
import com.stayFinder.proyectoFinal.security.UserDetailsImpl;
import com.stayFinder.proyectoFinal.services.reservaService.interfaces.ReservaServiceInterface;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@CrossOrigin(origins = "${frontend.url}")
@Tag(name = "Reservas", description = "Operaciones de creación, consulta, edición y eliminación de reservas")
public class ReservaController {

    private final ReservaServiceInterface reservaService;

    // ============================================
    // 🔹 Obtener todas las reservas del usuario
    // ============================================
    @GetMapping
    @Operation(summary = "Listar reservas del usuario autenticado", description = "Devuelve las reservas asociadas al usuario logueado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de reservas"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<List<ReservaResponseDTO>> getAll(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user) throws Exception {
        List<ReservaResponseDTO> reservas = reservaService.obtenerReservasUsuario(user.getId());
        return ResponseEntity.ok(reservas);
    }

    // ============================================
    // 🔹 Obtener una reserva por ID
    // ============================================
    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID", description = "Devuelve los datos de una reserva por su ID.")
    public ResponseEntity<ReservaResponseDTO> getById(
            @Parameter(description = "ID de la reserva", required = true, example = "1") @PathVariable Long id) {
        Optional<ReservaResponseDTO> reserva = reservaService.findById(id);
        return reserva.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ============================================
    // 🔹 Obtener reservas por alojamiento (para el anfitrión)
    // ============================================
    @GetMapping("/alojamiento/{alojamientoId}")
    @Operation(summary = "Listar reservas de un alojamiento", description = "Devuelve todas las reservas de un alojamiento específico (para el anfitrión).")
    public ResponseEntity<List<ReservaResponseDTO>> getByAlojamiento(
            @PathVariable Long alojamientoId) throws Exception {
        return ResponseEntity.ok(reservaService.obtenerReservasPorAlojamiento(alojamientoId));
    }

    // ============================================
    // 🔹 Crear reserva completa (con fechas y detalles)
    // ============================================
    @PostMapping
    @Operation(summary = "Crear reserva", description = "Crea una nueva reserva para el usuario autenticado u obtenido desde el DTO.")
    public ResponseEntity<ReservaResponseDTO> create(
            @Valid @RequestBody ReservaRequestDTO reserva,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user) throws Exception {
        
        Long userId = (user != null) ? user.getId() : reserva.usuarioId();
        if (userId == null) {
            throw new Exception("Error de autenticación: No se ha podido validar el usuario que crea la reserva.");
        }
        
        ReservaResponseDTO response = reservaService.createReserva(reserva, userId);
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 🔹 Crear reserva básica (solo alojamiento + fecha)
    // ============================================
    @PostMapping("/crear-basica")
    @Operation(summary = "Crear una reserva básica", description = "Crea una reserva con solo el ID del alojamiento y la fecha para el usuario autenticado.")
    public ResponseEntity<ReservaResponseDTO> crearReservaBasica(
            @RequestBody CreateReservaRequestDTO dto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user
    ) throws Exception {
        return ResponseEntity.ok(reservaService.createReservaBasica(dto, user.getId()));
    }

    // ============================================
    // 🔹 Confirmar reserva (OWNER/ADMIN)
    // ============================================
    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar reserva", description = "El anfitrión o administrador confirma una reserva en estado PENDIENTE.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reserva confirmada"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<Void> confirmar(
            @Parameter(description = "ID de la reserva", required = true) @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user) throws Exception {
        reservaService.confirmarReserva(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // 🔹 Cancelar reserva (CLIENT/OWNER/ADMIN)
    // ============================================
    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar reserva", description = "Cancela una reserva. El cliente puede cancelar la suya si faltan más de 48h. El anfitrión/admin puede cancelar cualquiera.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reserva cancelada"),
            @ApiResponse(responseCode = "400", description = "No se puede cancelar (dentro de las 48h)"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<Void> cancelar(
            @Parameter(description = "ID de la reserva", required = true) @PathVariable Long id,
            @RequestBody(required = false) CancelarReservaRequestDTO dto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user) throws Exception {
        CancelarReservaRequestDTO payload = dto != null ? dto : new CancelarReservaRequestDTO(id, null);
        reservaService.cancelarReserva(payload, user.getId());
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // 🔹 Actualizar reserva
    // ============================================
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar reserva", description = "Actualiza una reserva (ej. cambios de fechas).")
    public ResponseEntity<Object> update(
            @Parameter(description = "ID de la reserva", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto con los cambios de reserva") @RequestBody ReservaRequestDTO reserva)
            throws Exception {
        ReservaResponseDTO updated = reservaService.save(reserva);
        return ResponseEntity.ok(updated);
    }

    // ============================================
    // 🔹 Eliminar reserva
    // ============================================
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reserva", description = "Elimina una reserva por id. Requiere ser el dueño.")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la reserva", required = true, example = "1") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user) throws Exception {
        reservaService.deleteById(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}