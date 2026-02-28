package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta general para operaciones del sistema")
public record ControllerGeneralResponseDTO(
        @Schema(description = "Indica si la operación fue exitosa", example = "true") boolean success,
        @Schema(description = "Mensaje asociado a la operación", example = "Operación realizada correctamente") String message
) { }
