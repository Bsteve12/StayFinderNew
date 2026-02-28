package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.PasswordResetRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.PasswordResetTokenRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.PasswordResetTokenResponseDTO;
import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.repository.UsuarioRepository;
import com.stayFinder.proyectoFinal.services.passwordResetService.interfaces.PasswordResetServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetServiceInterface resetService;
    private final UsuarioRepository usuarioRepo; // Inyectado para buscar por email

    @PostMapping("/forgot-password")
    @Operation(summary = "Generar token de recuperación de contraseña mediante Email")
    @ApiResponse(responseCode = "200", description = "Token generado y enviado al correo")
    public PasswordResetTokenResponseDTO forgotPassword(@RequestBody PasswordResetRequestDTO dto) throws Exception {
        // 1. Buscamos al usuario por el email que llega del front
        Usuario usuario = usuarioRepo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new Exception("El correo electrónico no está registrado."));

        // 2. Mapeamos al DTO que ya usa tu servicio
        PasswordResetTokenRequestDTO tokenDto = PasswordResetTokenRequestDTO.builder()
                .usuarioId(usuario.getId())
                .build();

        // 3. Llamamos al servicio para generar token y enviar email
        return resetService.generarToken(tokenDto);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña con token")
    @ApiResponse(responseCode = "200", description = "Contraseña restablecida correctamente")
    public org.springframework.http.ResponseEntity<java.util.Map<String, String>> resetPassword(@RequestParam String token, @RequestParam String nuevaPassword) throws Exception {
        resetService.resetPassword(token, nuevaPassword);
        java.util.Map<String, String> response = new java.util.HashMap<>();
        response.put("mensaje", "Contraseña restablecida correctamente");
        return org.springframework.http.ResponseEntity.ok(response);
    }
}