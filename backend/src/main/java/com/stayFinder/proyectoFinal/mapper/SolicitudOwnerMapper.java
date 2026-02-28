package com.stayFinder.proyectoFinal.mapper;

import com.stayFinder.proyectoFinal.dto.outputDTO.SolicitudOwnerResponseDTO;
import com.stayFinder.proyectoFinal.entity.SolicitudOwner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudOwnerMapper {

    @Mapping(source = "usuario.nombre", target = "nombreUsuario")
    SolicitudOwnerResponseDTO toDto(SolicitudOwner s);
}
