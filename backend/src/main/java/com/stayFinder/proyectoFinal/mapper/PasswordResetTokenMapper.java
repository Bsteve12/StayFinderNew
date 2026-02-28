package com.stayFinder.proyectoFinal.mapper;

import com.stayFinder.proyectoFinal.dto.outputDTO.PasswordResetTokenResponseDTO;
import com.stayFinder.proyectoFinal.entity.PasswordResetToken;
import org.springframework.stereotype.Component;

@Component
public class PasswordResetTokenMapper {

    public PasswordResetTokenResponseDTO toDto(PasswordResetToken entity) {
        return PasswordResetTokenResponseDTO.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuario().getId())
                .token(entity.getToken())
                .expiresAt(entity.getExpiresAt())
                .usado(entity.isUsado())  //
                .build();
    }
}
