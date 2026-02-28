package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;

public record SolicitudPublicacionRequestDTO(
        @Schema(description = "ID del usuario que hace la solicitud", example = "2")
        Long usuarioId,

        @Schema(description = "ID del alojamiento a publicar", example = "10")
        Long alojamientoId,

        @Schema(description = "Comentario opcional del solicitante", example = "Deseo publicar mi finca para alquiler")
        String comentario
) {}
