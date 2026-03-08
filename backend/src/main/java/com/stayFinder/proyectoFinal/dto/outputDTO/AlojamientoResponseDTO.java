package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "Respuesta con los datos de un alojamiento e imágenes asociadas")
public class AlojamientoResponseDTO {

    @Schema(description = "ID del alojamiento", example = "10")
    private Long id;

    @Schema(description = "Nombre del alojamiento", example = "Casa en el Lago")
    private String nombre;

    @Schema(description = "Dirección del alojamiento", example = "Calle 123 #45-67, Armenia")
    private String direccion;

    @Schema(description = "Precio por noche", example = "150000.0")
    private Double precio;

    @Schema(description = "Descripción del alojamiento", example = "Hermosa casa con vista al lago y piscina privada")
    private String descripcion;

    @Schema(description = "Capacidad máxima de huéspedes", example = "6")
    private Integer capacidadMaxima;

    @Schema(description = "ID del propietario", example = "3")
    private Long ownerId;

    @Schema(description = "Lista de imágenes asociadas al alojamiento")
    private List<ImagenAlojamientoResponseDTO> imagenes;

    @Schema(description = "Lista de servicios asociados al alojamiento")
    private List<ServicioResponseDTO> servicios;

    @Schema(description = "Estado actual de publicación")
    private String estado;
}
