package com.stayFinder.proyectoFinal.mapper;

import com.stayFinder.proyectoFinal.dto.inputDTO.ReservaRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ReservaResponseDTO;
import com.stayFinder.proyectoFinal.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservaMapper {


    @Mapping(source = "usuario.usuarioId", target = "usuarioId")
    @Mapping(source = "alojamiento.id", target = "alojamientoId")
    ReservaResponseDTO toDto(Reserva reserva);

    // De DTO a Entity (el servicio seteará usuario y alojamiento)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "alojamiento", ignore = true)
    @Mapping(target = "estado", ignore = true) // Estado lo maneja el servicio
    Reserva toEntity(ReservaRequestDTO dto);
}