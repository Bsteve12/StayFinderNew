package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Relación entre alojamiento y servicio")
public class AlojamientoServicioResponseDTO {

    @NonNull
    @Schema(description = "ID del alojamiento", example = "5")
    private Long alojamientoId;

    @NonNull
    @Schema(description = "ID del servicio", example = "2")
    private Long servicioId;

    @NonNull
    @Schema(description = "Nombre del servicio", example = "WiFi")
    private String servicioNombre;
}
