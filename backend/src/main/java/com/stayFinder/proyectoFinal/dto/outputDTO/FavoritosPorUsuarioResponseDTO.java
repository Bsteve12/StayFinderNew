package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Muestra los alojamientos favoritos asociados a un usuario y la cantidad total de favoritos")
public class FavoritosPorUsuarioResponseDTO {

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String nombreUsuario;

    @Schema(description = "Nombre del alojamiento favorito", example = "Cabaña Los Pinos")
    private String alojamiento;

    @Schema(description = "Cantidad total de alojamientos marcados como favoritos", example = "5")
    private Long cantidadFavoritos;

    // 🔹 Constructor adicional para consultas parciales (nombreUsuario y alojamiento)
    public FavoritosPorUsuarioResponseDTO(String nombreUsuario, String alojamiento) {
        this.nombreUsuario = nombreUsuario;
        this.alojamiento = alojamiento;
    }
}
