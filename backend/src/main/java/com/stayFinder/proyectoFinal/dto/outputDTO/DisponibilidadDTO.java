package com.stayFinder.proyectoFinal.dto.outputDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadDTO {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String tipo; // "RESERVA" o "BLOQUEO"
    private String estado; // e.g., "CONFIRMADA", "MANTENIMIENTO"
}
