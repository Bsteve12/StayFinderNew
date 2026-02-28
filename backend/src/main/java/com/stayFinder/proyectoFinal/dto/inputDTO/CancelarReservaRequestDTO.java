package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CancelarReservaRequestDTO(

        @NotNull
        @Schema(description = "ID de la reserva a cancelar", example = "15")
        Long reservaId,

        @Schema(description = "Motivo de la cancelación", example = "El viaje fue pospuesto")
        String motivo
) {}
