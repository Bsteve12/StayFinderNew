package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta con los datos de un mensaje dentro de un chat")
public class MensajeResponseDTO {
    @Schema(description = "ID del mensaje", example = "101")
    private Long id;

    @Schema(description = "ID del chat al que pertenece el mensaje", example = "15")
    private Long chatId;

    @Schema(description = "ID del remitente del mensaje", example = "7")
    private Long remitenteId;

    @Schema(description = "Contenido del mensaje", example = "Hola, ¿a qué hora es el check-in?")
    private String contenido;

    @Schema(description = "Fecha y hora en que se envió el mensaje", example = "2025-09-12T18:45:00")
    private LocalDateTime fechaEnvio;
}
