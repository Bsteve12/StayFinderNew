package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Respuesta con los datos de un alojamiento marcado como favorito")
public class FavoriteResponseDTO {
    @Schema(description = "ID del registro de favorito", example = "12")
    private Long id;

    @Schema(description = "ID del usuario que marcó el favorito", example = "4")
    private Long usuarioId;

    @Schema(description = "ID del alojamiento marcado como favorito", example = "9")
    private Long alojamientoId;
}
