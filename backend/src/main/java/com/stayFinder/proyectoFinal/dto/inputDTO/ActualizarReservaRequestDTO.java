package com.stayFinder.proyectoFinal.dto.inputDTO;

import com.stayFinder.proyectoFinal.entity.enums.TipoReserva;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ActualizarReservaRequestDTO(

        @NotNull
        @Schema(description = "ID de la reserva a actualizar", example = "15")
        Long reservaId,

        @NotNull
        @Future
        @Schema(description = "Nueva fecha de inicio de la reserva", example = "2025-12-20")
        LocalDate fechaInicio,

        @NotNull
        @Future
        @Schema(description = "Nueva fecha de fin de la reserva", example = "2025-12-25")
        LocalDate fechaFin,

        @NotNull
        @Schema(description = "Cantidad de huéspedes actualizada", example = "3")
        Integer numeroHuespedes,

        @NotNull
        @Schema(description = "Tipo de reserva (VIP o SENCILLA)", example = "VIP")
        TipoReserva tipoReserva
) {}
