package com.stayFinder.proyectoFinal.dto.inputDTO;

import com.stayFinder.proyectoFinal.entity.enums.EstadoReserva;
import java.time.LocalDate;

public record HistorialReservasRequestDTO(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        EstadoReserva estado
) {}
