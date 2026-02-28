package com.stayFinder.proyectoFinal.dto.outputDTO;

import com.stayFinder.proyectoFinal.entity.enums.EstadoSolicitudPublicacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
@Schema(description = "Datos de respuesta de la solicitud de la publicacion ")
public record SolicitudPublicacionResponseDTO(
        Long id,
        String nombreUsuario,
        String titulo,
        EstadoSolicitudPublicacion estado,
        String comentario,
        LocalDateTime fechaSolicitud,
        LocalDateTime fechaRevision
) {}
