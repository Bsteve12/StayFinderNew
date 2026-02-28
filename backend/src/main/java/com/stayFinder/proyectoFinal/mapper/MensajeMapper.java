package com.stayFinder.proyectoFinal.mapper;

import com.stayFinder.proyectoFinal.dto.outputDTO.MensajeResponseDTO;
import com.stayFinder.proyectoFinal.entity.Mensaje;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MensajeMapper {

    @Mapping(source = "chat.id", target = "chatId")
    @Mapping(source = "remitente.id", target = "remitenteId")
    MensajeResponseDTO toDto(Mensaje mensaje);
}
