package com.stayFinder.proyectoFinal.mapper;

import com.stayFinder.proyectoFinal.dto.inputDTO.ServicioRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ServicioResponseDTO;
import com.stayFinder.proyectoFinal.entity.Servicio;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServicioMapper {
    Servicio toEntity(ServicioRequestDTO dto);
    ServicioResponseDTO toDto(Servicio entity);
}
