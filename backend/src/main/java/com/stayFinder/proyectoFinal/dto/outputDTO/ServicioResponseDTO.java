package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta con los datos de un servicio disponible en un alojamiento")
public class ServicioResponseDTO {
    @Schema(description = "ID del servicio", example = "10")
    private Long id;

    @Schema(description = "Nombre del servicio", example = "WiFi")
    private String nombre;

    @Schema(description = "Descripción del servicio", example = "Internet de alta velocidad en toda la propiedad")
    private String descripcion;

    @Schema(description = "precio del servicio", example = "El servicio o chef de aseo cuesta 80 mil pesos la noche  ")
    private Double precio;
}
