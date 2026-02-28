package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.PasswordResetTokenRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.PasswordResetTokenResponseDTO;
import com.stayFinder.proyectoFinal.services.passwordTokenService.interfaces.PasswordResetTokenServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/password-tokens")
@RequiredArgsConstructor
public class PasswordResetTokenController {

    private final PasswordResetTokenServiceInterface tokenService;

    @GetMapping
    @Operation(summary = "Listar tokens de recuperación de contraseña")
    @ApiResponse(responseCode = "200", description = "Listado de tokens")
    public List<PasswordResetTokenResponseDTO> listarTokens() {
        return tokenService.listarTokens();
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo token de recuperación")
    @ApiResponse(responseCode = "201", description = "Token creado")
    public PasswordResetTokenResponseDTO crearToken(@RequestBody PasswordResetTokenRequestDTO dto) throws Exception {
        return tokenService.crearToken(dto);
    }

    @GetMapping("/validar/{token}")
    @Operation(summary = "Validar un token")
    public ResponseEntity<PasswordResetTokenResponseDTO> validar(@PathVariable String token) throws Exception {
        return ResponseEntity.ok(tokenService.validarToken(token));
    }

    @PostMapping("/usar/{token}")
    @Operation(summary = "Marcar token como usado")
    public ResponseEntity<Void> usar(@PathVariable String token) throws Exception {
        tokenService.marcarComoUsado(token);
        return ResponseEntity.noContent().build();
    }
}
