package com.stayFinder.proyectoFinal.mapper;

import com.stayFinder.proyectoFinal.dto.inputDTO.ComentarioRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ComentarioResponseDTO;
import com.stayFinder.proyectoFinal.entity.Comentario;
import com.stayFinder.proyectoFinal.exceptionHandling.advices.ResourceNotFoundException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ComentarioMapper {

    @Mapping(target = "nombreUsuario", expression = "java(validarUsuarioNombre(comentario))")
    @Mapping(target = "mensaje", source = "mensaje")
    @Mapping(target = "calificacion", source = "calificacion")
    @Mapping(target = "respuestaAnfitrion", source = "respuestaAnfitrion")
    @Mapping(target = "nombreAnfitrion", expression = "java(validarAnfitrionNombre(comentario))")
    @Mapping(target = "fechaCreacion", source = "fechaCreacion")
    ComentarioResponseDTO toDto(Comentario comentario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "alojamiento", ignore = true)
    @Mapping(target = "reserva", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "respuestaAnfitrion", ignore = true)
    Comentario toEntity(ComentarioRequestDTO dto);

    // -------- Helpers con validaciones --------
    default String validarUsuarioNombre(Comentario comentario) {
        if (comentario.getUsuario() == null) {
            throw new com.stayFinder.proyectoFinal.exceptionHandling.advices.ResourceNotFoundException(
                    "El comentario no tiene usuario válido"
            );
        }
        return comentario.getUsuario().getNombre();
    }

    default String validarAnfitrionNombre(Comentario comentario) {
        if (comentario.getAlojamiento() == null || comentario.getAlojamiento().getOwner() == null) {
            throw new com.stayFinder.proyectoFinal.exceptionHandling.advices.ResourceNotFoundException(
                    "El comentario no tiene un anfitrión válido"
            );
        }
        return comentario.getAlojamiento().getOwner().getNombre();
    }
}
