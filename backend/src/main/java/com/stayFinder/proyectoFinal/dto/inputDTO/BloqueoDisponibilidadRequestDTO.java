package com.stayFinder.proyectoFinal.dto.inputDTO;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BloqueoDisponibilidadRequestDTO(
        @NotNull Long alojamientoId,
        @NotNull LocalDate fechaInicio,
        @NotNull LocalDate fechaFin,
        String motivo) {
}
