package com.stayFinder.proyectoFinal.dto.inputDTO;

import com.stayFinder.proyectoFinal.entity.enums.TipoReserva;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Datos para crear una reserva")
public record ReservaRequestDTO(
        @NotNull @Schema(description = "ID del usuario que reserva", example = "3") Long usuarioId,
        @NotNull @Schema(description = "ID del alojamiento", example = "10") Long alojamientoId,
        @NotNull @Schema(description = "Fecha de inicio (check-in) en formato ISO", example = "2025-10-20") LocalDate fechaInicio,
        @NotNull @Schema(description = "Fecha de fin (check-out) en formato ISO", example = "2025-10-23") LocalDate fechaFin,
        @NotNull @Min(value = 1) @Schema(description = "Número de huéspedes", example = "2") Integer numeroHuespedes,
        @NotNull @Schema(description = "Tipo de reserva: VIP o SENCILLA", example = "VIP") TipoReserva tipoReserva
) {}
