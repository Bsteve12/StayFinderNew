package com.stayFinder.proyectoFinal.services.passwordTokenService.interfaces;

import com.stayFinder.proyectoFinal.dto.inputDTO.PasswordResetTokenRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.PasswordResetTokenResponseDTO;

import java.util.List;

public interface PasswordResetTokenServiceInterface {
    List<PasswordResetTokenResponseDTO> listarTokens();
    PasswordResetTokenResponseDTO crearToken(PasswordResetTokenRequestDTO dto) throws Exception;
    PasswordResetTokenResponseDTO validarToken(String token) throws Exception;
    void marcarComoUsado(String token) throws Exception;
}
