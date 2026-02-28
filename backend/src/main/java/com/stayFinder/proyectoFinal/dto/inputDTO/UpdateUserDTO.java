package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateUserDTO(
        @Schema(description = "ID del usuario a actualizar", example = "5")
        Long id,
        @Schema(description = "Nuevo nombre del usuario", example = "Juan Pérez")
        String nombre,
        @Schema(description = "Nuevo número de teléfono", example = "3216549870")
        String telefono,
        @Schema(description = "Nueva fecha de nacimiento", example = "1998-04-21")
        String fechaNacimiento,
        @Schema(description = "Nueva contraseña", example = "Password123")
        String contrasena,

        @Schema(description = "Nueva cedula del usuario", example = "0987654321")
        Long usuarioId
) { }