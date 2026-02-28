package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de entrada para registrar un nuevo servicio disponible en un alojamiento")
public class ServicioInputDTO {
    @NotBlank
    @Schema(description = "Nombre del servicio", example = "Piscina")
    private String nombre;

    @Schema(description = "Descripción del servicio", example = "Piscina privada disponible para los huéspedes")
    private String descripcion;
}