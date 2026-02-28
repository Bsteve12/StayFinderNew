package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Datos de entrada para crear una publicación")
public class PublicacionRequestDTO {
    @NotBlank
    @Schema(description = "Título de la publicación", example = "Hermosa finca en el Quindío")
    private String titulo;

    @NotBlank
    @Schema(description = "Descripción detallada de la publicación", example = "Alojamiento ideal para vacaciones, con piscina y zonas verdes")
    private String descripcion;

    @NotNull
    @Schema(description = "ID del usuario que crea la publicación", example = "2")
    private Long usuarioId;
}
