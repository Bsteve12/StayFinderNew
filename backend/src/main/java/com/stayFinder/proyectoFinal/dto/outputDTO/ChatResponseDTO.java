package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta con los datos de un chat entre usuario y anfitrión")
public class ChatResponseDTO {

    @Schema(description = "ID del chat", example = "15")
    private Long id;

    @Schema(description = "ID del usuario", example = "7")
    private Long usuarioId;

    @Schema(description = "ID del anfitrión", example = "3")
    private Long anfitrionId;
}
