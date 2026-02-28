package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import com.stayFinder.proyectoFinal.entity.enums.EstadoSolicitudPublicacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con la información de una publicación")
public class PublicacionResponseDTO {

    @Schema(description = "ID de la publicación", example = "120")
    private Long id;

    @Schema(description = "Título de la publicación", example = "Hermosa finca en el Quindío")
    private String titulo;

    @Schema(description = "Descripción de la publicación", example = "Alojamiento con piscina y zona de BBQ")
    private String descripcion;

    @Schema(description = "Estado de la publicación", example = "APROBADA")
    private EstadoSolicitudPublicacion estado;

    @Schema(description = "Nombre del usuario que publicó", example = "Carlos Pérez")
    private String nombreUsuario;
}
