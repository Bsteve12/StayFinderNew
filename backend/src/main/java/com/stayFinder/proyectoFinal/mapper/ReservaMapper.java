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
    @Mapping(source = "alojamiento.nombre", target = "alojamientoNombre")
    @Mapping(expression = "java(reserva.getAlojamiento() != null && reserva.getAlojamiento().getImagenes() != null && !reserva.getAlojamiento().getImagenes().isEmpty() ? reserva.getAlojamiento().getImagenes().get(0).getUrl() : null)", target = "alojamientoImagen")
    ReservaResponseDTO toDto(Reserva reserva);

    // De DTO a Entity (el servicio seteará usuario y alojamiento)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "alojamiento", ignore = true)
    @Mapping(target = "estado", ignore = true) // Estado lo maneja el servicio
    Reserva toEntity(ReservaRequestDTO dto);
}