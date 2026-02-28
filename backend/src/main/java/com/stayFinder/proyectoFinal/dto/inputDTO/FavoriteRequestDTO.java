package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos de entrada para marcar un alojamiento como favorito")
public record FavoriteRequestDTO(
        @NotNull @Schema(description = "ID del usuario que guarda el favorito", example = "2") Long usuarioId,
        @NotNull @Schema(description = "ID del alojamiento que se guarda en favoritos", example = "10") Long alojamientoId
) {}
