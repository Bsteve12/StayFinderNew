package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Datos de entrada para asociar una imagen a un alojamiento")
public class ImagenAlojamientoRequestDTO {
    @NotNull
    @Schema(description = "ID del alojamiento al que pertenece la imagen", example = "5")
    private Long alojamientoId;

    @NotBlank
    @Schema(description = "URL de la imagen", example = "https://cdn.stayfinder.com/alojamientos/imagen1.jpg")
    private String url;
}
