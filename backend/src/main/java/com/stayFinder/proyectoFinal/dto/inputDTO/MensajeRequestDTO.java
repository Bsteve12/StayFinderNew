package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de entrada para enviar un mensaje en un chat")
public class MensajeRequestDTO {
    @NotNull
    @Schema(description = "ID del chat al que pertenece el mensaje", example = "3")
    private Long chatId;

    @NotNull
    @Schema(description = "ID del remitente (usuario o anfitrión)", example = "7")
    private Long remitenteId;

    @NotBlank
    @Schema(description = "Contenido del mensaje", example = "Hola, quisiera confirmar mi reserva")
    private String contenido;
}
