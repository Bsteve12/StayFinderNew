package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de entrada para crear un chat entre un usuario y un anfitrión")
public class ChatRequestDTO {

    @Schema(description = "ID del usuario que inicia el chat", example = "5")
    private Long usuarioId;

    @Schema(description = "ID del anfitrión con el que se abre el chat", example = "2")
    private Long anfitrionId;
}
