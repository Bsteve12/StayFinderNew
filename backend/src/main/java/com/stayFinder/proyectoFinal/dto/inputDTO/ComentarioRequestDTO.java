package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import lombok.Builder;

@Builder
@Schema(description = "Datos de entrada para registrar un comentario en una reserva")
public record ComentarioRequestDTO(

        @NotNull
        @Schema(description = "ID de la reserva asociada al comentario", example = "42")
        Long reservaId,

        @NotNull
        @Min(value = 1, message = "La calificación mínima es 1")
        @Max(value = 5, message = "La calificación máxima es 5")
        @Schema(description = "Calificación del alojamiento (1 a 5 estrellas)", example = "5")
        Integer calificacion,

        @NotBlank
        @Schema(description = "Mensaje o reseña del usuario sobre el alojamiento", example = "Excelente atención y lugar muy cómodo.")
        String mensaje
) {}
