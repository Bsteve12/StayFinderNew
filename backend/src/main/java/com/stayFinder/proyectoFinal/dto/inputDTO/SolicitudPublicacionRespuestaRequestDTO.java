package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;

public record SolicitudPublicacionRespuestaRequestDTO(
        @Schema(description = "ID de la solicitud a responder", example = "5")
        Long solicitudId,

        @Schema(description = "ID del admin que revisa la solicitud", example = "1")
        Long adminId,

        @Schema(description = "¿La solicitud fue aprobada?", example = "true")
        boolean aprobada,

        @Schema(description = "Comentario del admin", example = "Cumple con los requisitos")
        String comentario
) {}