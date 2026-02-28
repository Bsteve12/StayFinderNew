package com.stayFinder.proyectoFinal.services.passwordResetService.interfaces;

import com.stayFinder.proyectoFinal.dto.inputDTO.PasswordResetTokenRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.PasswordResetTokenResponseDTO;

public interface PasswordResetServiceInterface {
    PasswordResetTokenResponseDTO generarToken(PasswordResetTokenRequestDTO dto) throws Exception;
    void resetPassword(String token, String nuevaPassword) throws Exception;
}
