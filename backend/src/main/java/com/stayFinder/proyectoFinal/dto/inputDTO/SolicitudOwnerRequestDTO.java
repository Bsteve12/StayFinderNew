package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para crear una solicitud de Owner")
public record SolicitudOwnerRequestDTO(
        @Schema(description = "ID del usuario que solicita", example = "3") Long usuarioId,
        @Schema(description = "Comentario o descripción adicional", example = "Documentación para verificación") String comentario
) {}
