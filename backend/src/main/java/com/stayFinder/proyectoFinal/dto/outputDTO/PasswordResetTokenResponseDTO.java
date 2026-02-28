package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta con la información de un token de restablecimiento de contraseña")
public class PasswordResetTokenResponseDTO {

    @Schema(description = "ID del token de recuperación", example = "301")
    private Long id;

    @Schema(description = "ID del usuario asociado", example = "15")
    private Long usuarioId;

    @Schema(description = "Token de recuperación", example = "abc123xyz890resetToken")
    private String token;

    @Schema(description = "Fecha y hora de expiración del token", example = "2025-09-12T22:00:00")
    private LocalDateTime expiresAt;

    @Schema(description = "Indica si el token ya fue usado", example = "false")
    private boolean usado;
}
