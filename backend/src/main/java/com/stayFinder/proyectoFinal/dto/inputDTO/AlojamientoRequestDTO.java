package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlojamientoRequestDTO(
        @NotBlank
        @Schema(description = "Nombre del alojamiento", example = "Casa Campestre en Armenia")
        String nombre,

        @NotBlank
        @Schema(description = "Dirección del alojamiento", example = "Calle 10 #15-30, Armenia, Quindío")
        String direccion,

        @NotNull
        @Schema(description = "Precio por noche en USD", example = "120.5")
        Double precio,

        @Schema(description = "Descripción del alojamiento", example = "Hermosa finca con piscina y vista a las montañas")
        String descripcion,

        @NotNull
        @Schema(description = "Capacidad máxima de huéspedes", example = "6")
        Integer capacidadMaxima
) {}
