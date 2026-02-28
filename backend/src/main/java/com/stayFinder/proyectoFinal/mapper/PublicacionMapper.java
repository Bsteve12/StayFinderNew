package com.stayFinder.proyectoFinal.mapper;

import com.stayFinder.proyectoFinal.dto.inputDTO.PublicacionRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.PublicacionResponseDTO;
import com.stayFinder.proyectoFinal.entity.Publicacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PublicacionMapper {

    // De RequestDTO -> Entity: ignoramos la asociación usuario (se setea en servicio)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "estado", ignore = true) // el servicio decide el estado (PENDIENTE/APROBADA)
    Publicacion toEntity(PublicacionRequestDTO dto);

    // De Entity -> ResponseDTO: mapeamos nombre del usuario a nombreUsuario (si tu DTO lo tiene)
    @Mapping(source = "usuario.nombre", target = "nombreUsuario")
    PublicacionResponseDTO toDto(Publicacion publicacion);
}
