package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Muestra el nombre del usuario y la cantidad total de reservas que tiene")
public class ReservasPorUsuarioResponseDTO {

    @Schema(description = "Nombre completo del usuario", example = "Laura Gómez")
    private String nombreUsuario;

    @Schema(description = "Cantidad de reservas asociadas al usuario", example = "8")
    private Long cantidadReservas;
}
