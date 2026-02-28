package com.stayFinder.proyectoFinal.dto.inputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para asociar un servicio a un alojamiento")
public class AlojamientoServicioRequestDTO {
    @NonNull
    @Schema(description = "ID del alojamiento", example = "3")
    private Long alojamientoId;

    @NonNull
    @Schema(description = "ID del servicio", example = "1")
    private Long servicioId;
}
