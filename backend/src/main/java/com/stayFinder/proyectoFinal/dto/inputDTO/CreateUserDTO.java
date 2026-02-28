package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserDTO(
        @NotBlank @Size(max = 30)
        @Schema(description = "Nombre completo del usuario", example = "Juan Pérez") String nombre,

        @Email
        @Schema(description = "Correo electrónico único del usuario", example = "juan.perez@example.com") String correo,

        @Schema(description = "Número de teléfono", example = "+57 3123456789") String telefono,

        @Schema(description = "Fecha de nacimiento en formato YYYY-MM-DD", example = "1995-08-21") String fechaNacimiento,

        @NotBlank @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
        @Schema(description = "Contraseña segura", example = "Password123") String contrasena,
        @Schema(description = "Cedula del usuario", example = "1234567890") Long usuarioId
) {}
