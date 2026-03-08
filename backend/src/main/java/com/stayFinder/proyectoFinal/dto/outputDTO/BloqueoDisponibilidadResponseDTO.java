package com.stayFinder.proyectoFinal.dto.outputDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloqueoDisponibilidadResponseDTO {
    private Long id;
    private Long alojamientoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String motivo;
}
