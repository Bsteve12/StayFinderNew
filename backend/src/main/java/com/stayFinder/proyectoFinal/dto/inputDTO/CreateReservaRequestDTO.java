package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record CreateReservaRequestDTO(
        @NotNull @Schema(description = "ID del alojamiento a reservar", example = "4") Long alojamientoId,
        @Future @Schema(description = "Fecha de la reserva en formato YYYY-MM-DD", example = "2025-11-20") String fecha
) {}
