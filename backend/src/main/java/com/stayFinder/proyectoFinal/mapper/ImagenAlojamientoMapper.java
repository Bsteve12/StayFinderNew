package com.stayFinder.proyectoFinal.mapper;

import com.stayFinder.proyectoFinal.dto.inputDTO.ImagenAlojamientoRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ImagenAlojamientoResponseDTO;
import com.stayFinder.proyectoFinal.entity.ImagenAlojamiento;
import org.springframework.stereotype.Component;

@Component
public class ImagenAlojamientoMapper {

    public ImagenAlojamiento toEntity(ImagenAlojamientoRequestDTO dto) {
        ImagenAlojamiento imagen = new ImagenAlojamiento();
        imagen.setUrl(dto.getUrl());
        return imagen;
    }

    public ImagenAlojamientoResponseDTO toDto(ImagenAlojamiento entity) {
        ImagenAlojamientoResponseDTO dto = new ImagenAlojamientoResponseDTO();
        dto.setId(entity.getId());
        dto.setUrl(entity.getUrl());
        dto.setAlojamientoId(entity.getAlojamiento().getId());
        return dto;
    }
}
