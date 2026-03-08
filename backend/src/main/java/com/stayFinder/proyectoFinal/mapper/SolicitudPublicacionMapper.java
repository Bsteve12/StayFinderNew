package com.stayFinder.proyectoFinal.mapper;

import com.stayFinder.proyectoFinal.dto.outputDTO.SolicitudPublicacionResponseDTO;
import com.stayFinder.proyectoFinal.entity.SolicitudPublicacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudPublicacionMapper {



        @Mapping(source = "usuario.nombre", target = "nombreUsuario")
        @Mapping(source = "alojamiento.id", target = "alojamientoId")
        SolicitudPublicacionResponseDTO toDto(SolicitudPublicacion solicitud);
    }
