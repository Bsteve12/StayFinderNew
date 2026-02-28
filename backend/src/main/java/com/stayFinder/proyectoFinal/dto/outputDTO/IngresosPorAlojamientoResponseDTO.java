package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta con la información de los ingresos por alojamiento")
public class IngresosPorAlojamientoResponseDTO {
    private String nombreAlojamiento;
    private Double ingresosTotales;
}
