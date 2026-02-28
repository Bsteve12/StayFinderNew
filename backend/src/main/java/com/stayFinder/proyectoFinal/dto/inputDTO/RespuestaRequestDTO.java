package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "Datos de entrada para registrar una respuesta del anfitrión a un comentario")
public record RespuestaRequestDTO(

        @NotNull
        @Schema(description = "ID del comentario al que se responde", example = "15")
        Long comentarioId,

        @NotBlank
        @Schema(description = "Mensaje de respuesta del anfitrión", example = "Gracias por tu reseña, fue un placer recibirte.")
        String mensajeRespuesta
) {}
