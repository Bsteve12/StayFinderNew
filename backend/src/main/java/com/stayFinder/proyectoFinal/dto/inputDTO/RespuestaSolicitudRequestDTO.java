package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para que el admin responda una solicitud")
public record RespuestaSolicitudRequestDTO(
        @Schema(description = "ID de la solicitud", example = "10") Long solicitudId,
        @Schema(description = "ID del admin que responde", example = "1") Long adminId,
        @Schema(description = "¿Aprobada?", example = "true") boolean aprobada,
        @Schema(description = "Comentario del admin", example = "Documentación correcta") String comentario
) {}
