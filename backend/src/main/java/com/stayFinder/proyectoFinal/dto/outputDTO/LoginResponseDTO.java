package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta al iniciar sesión con éxito")
public record LoginResponseDTO(
        @Schema(description = "Token JWT generado tras autenticación exitosa",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String token
) { }
