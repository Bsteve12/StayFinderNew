package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Solicitud para enviar un correo de recuperación de contraseña")
public class PasswordResetRequestDTO {
    @NotBlank
    @Email
    @Schema(description = "Email del usuario que quiere recuperar la contraseña", example = "usuario@gmail.com")
    private String email;
}
