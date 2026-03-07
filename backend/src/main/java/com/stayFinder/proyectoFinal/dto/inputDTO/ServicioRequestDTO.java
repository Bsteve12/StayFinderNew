package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ServicioRequestDTO(
        @NotBlank @Schema(description = "Nombre del servicio", example = "Chef")
        String nombre,

        @NotBlank @Schema(description = "Descripción del servicio", example = "Servicio de chef profesional a domicilio")
        String descripcion,

        @NotNull @Schema(description = "Precio del servicio por día", example = "80000.0")
        Double precio

        
) {}
