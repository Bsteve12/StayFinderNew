package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Respuesta con la información de una imagen de un alojamiento")
public class ImagenAlojamientoResponseDTO {
    @Schema(description = "ID de la imagen", example = "30")
    private Long id;

    @Schema(description = "URL de la imagen", example = "http://example.com/imagenes/casa1.jpg")
    private String url;

    @Schema(description = "ID del alojamiento asociado", example = "5")
    private Long alojamientoId;
}
