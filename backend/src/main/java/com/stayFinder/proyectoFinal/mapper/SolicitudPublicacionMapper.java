package com.stayFinder.proyectoFinal.mapper;

import com.stayFinder.proyectoFinal.dto.outputDTO.SolicitudPublicacionResponseDTO;
import com.stayFinder.proyectoFinal.entity.SolicitudPublicacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudPublicacionMapper {



        @Mapping(source = "usuario.nombre", target = "nombreUsuario")

        SolicitudPublicacionResponseDTO toDto(SolicitudPublicacion solicitud);
    }
