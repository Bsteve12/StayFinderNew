package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import com.stayFinder.proyectoFinal.entity.enums.Role;

import java.time.LocalDate;

@Schema(description = "Respuesta con datos básicos de usuario")
public record UsuarioResponseDTO(
        @Schema(description = "ID del usuario", example = "3") Long id,
        @Schema(description = "Nombre completo", example = "Juan Pérez") String nombre,
        @Schema(description = "Correo electrónico", example = "juan@example.com") String correo,
        @Schema(description = "Teléfono", example = "+573001112233") String telefono,
        @Schema(description = "Fecha de nacimiento", example = "1995-05-20") LocalDate fechaNacimiento,
        @Schema(description = "Cédula del usuario", example = "1234567890") Long usuarioId,
        @Schema(description = "Rol del usuario", example = "CLIENT") Role role
) {}
