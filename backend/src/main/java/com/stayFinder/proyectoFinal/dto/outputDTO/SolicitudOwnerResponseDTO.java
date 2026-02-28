package com.stayFinder.proyectoFinal.dto.outputDTO;

import com.stayFinder.proyectoFinal.entity.enums.EstadoSolicitud;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Respuesta con los datos de una solicitud para convertirse en propietario (Owner)")
public record SolicitudOwnerResponseDTO(

        @Schema(description = "ID de la solicitud", example = "45")
        Long id,

        @Schema(description = "Nombre del usuario que envió la solicitud", example = "Andrés Torres")
        String nombreUsuario,

        @Schema(description = "Estado actual de la solicitud", example = "EN_REVISION")
        EstadoSolicitud estado,

        @Schema(description = "Comentario o motivo de la solicitud", example = "Quiero publicar mis propiedades en la plataforma.")
        String comentario,

        @Schema(description = "Ruta o URL del documento cargado como soporte", example = "https://cdn.stayfinder.com/documentos/solicitud45.pdf")
        String documentoRuta,

        @Schema(description = "Fecha en que se envió la solicitud", example = "2025-03-20T14:45:00")
        LocalDateTime fechaSolicitud,

        @Schema(description = "Fecha en que fue revisada la solicitud (si aplica)", example = "2025-03-22T09:10:00")
        LocalDateTime fechaRevision
) {}
