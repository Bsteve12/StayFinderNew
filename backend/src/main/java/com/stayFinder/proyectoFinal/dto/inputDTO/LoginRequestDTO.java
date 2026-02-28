package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @Email
        @NotBlank
        @Schema(description = "Correo electrónico del usuario", example = "juan.perez@example.com") String email,

        @NotBlank
        @Schema(description = "Contraseña del usuario", example = "Password123") String contrasena
) {}
