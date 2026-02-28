package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.CreateReservaRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ControllerGeneralResponseDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.ReservaRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ReservaResponseDTO;
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
@CrossOrigin(origins = "http://localhost:4200")
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
        // user.getId() es el ID de Negocio (usuarioId)
        List<ReservaResponseDTO> reservas = reservaService.obtenerReservasUsuario(user.getId());
        return ResponseEntity.ok(reservas);
    }

    // ============================================
    // 🔹 Obtener una reserva por ID
    // ============================================
    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID", description = "Devuelve los datos de una reserva por su ID.")
    public ResponseEntity<ReservaResponseDTO> getById(
            @Parameter(description = "ID de la reserva", required = true, example = "1")
            @PathVariable Long id) {
        Optional<ReservaResponseDTO> reserva = reservaService.findById(id);
        return reserva.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ============================================
    // 🔹 Crear reserva completa (con fechas y detalles)
    // ============================================
    @PostMapping
    @Operation(summary = "Crear reserva", description = "Crea una nueva reserva para el usuario autenticado.")
    public ResponseEntity<ReservaResponseDTO> create(
            @Valid @RequestBody ReservaRequestDTO reserva,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user) throws Exception {

        // user.getId() es el ID de Negocio (usuarioId)
        ReservaResponseDTO response = reservaService.createReserva(reserva, user.getId());
        return ResponseEntity.ok(response);
    }

    // ============================================
    // 🔹 Crear reserva básica (solo alojamiento + fecha)
    // ============================================
    @PostMapping("/crear-basica")
    @Operation(summary = "Crear una reserva básica", description = "Crea una reserva con solo el ID del alojamiento y la fecha para el usuario autenticado.")
    public ResponseEntity<ReservaResponseDTO> crearReservaBasica(
            @RequestBody CreateReservaRequestDTO dto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user // 👈 Corregido para obtener el ID del token
    ) throws Exception {
        // user.getId() es el ID de Negocio (usuarioId)
        return ResponseEntity.ok(reservaService.createReservaBasica(dto, user.getId()));
    }

    // ============================================
    // 🔹 Actualizar reserva
    // ============================================
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar reserva", description = "Actualiza una reserva (ej. cambios de fechas).")
    public ResponseEntity<Object> update(
            @Parameter(description = "ID de la reserva", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto con los cambios de reserva")
            @RequestBody ReservaRequestDTO reserva) throws Exception {

        // El método save(ReservaRequestDTO) usa el usuarioId del DTO. Se mantiene, pero la lógica ideal sería usar el ID del token.
        ReservaResponseDTO updated = reservaService.save(reserva);
        return ResponseEntity.ok(updated);
    }

    // ============================================
    // 🔹 Eliminar reserva
    // ============================================
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reserva", description = "Elimina una reserva por id. Requiere ser el dueño.")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la reserva", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user) throws Exception {
        // user.getId() es el ID de Negocio (usuarioId)
        reservaService.deleteById(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}