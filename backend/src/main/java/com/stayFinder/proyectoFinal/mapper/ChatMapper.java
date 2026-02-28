package com.stayFinder.proyectoFinal.mapper;

import com.stayFinder.proyectoFinal.dto.outputDTO.ChatResponseDTO;
import com.stayFinder.proyectoFinal.entity.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "anfitrion.id", target = "anfitrionId")
    ChatResponseDTO toDto(Chat chat);
}
