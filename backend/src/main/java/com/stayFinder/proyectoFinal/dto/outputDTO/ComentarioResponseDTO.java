package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Respuesta con los datos de un comentario realizado por un usuario")
public record ComentarioResponseDTO(

        @Schema(description = "Nombre del usuario que hizo el comentario", example = "Laura Gómez")
        String nombreUsuario,

        @Schema(description = "Mensaje del comentario", example = "Excelente lugar, muy limpio y tranquilo.")
        String mensaje,

        @Schema(description = "Calificación otorgada por el usuario (1 a 5)", example = "5")
        Integer calificacion,

        @Schema(description = "Respuesta del anfitrión al comentario (si existe)", example = "Gracias por tu visita, te esperamos de nuevo.")
        String respuestaAnfitrion,

        @Schema(description = "Nombre del anfitrión que respondió el comentario", example = "Carlos Pérez")
        String nombreAnfitrion,

        @Schema(description = "Fecha en que se realizó el comentario", example = "2025-03-14T10:30:00")
        LocalDateTime fechaCreacion
) {}
