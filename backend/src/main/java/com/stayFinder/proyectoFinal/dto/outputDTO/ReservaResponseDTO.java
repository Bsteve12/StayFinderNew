package com.stayFinder.proyectoFinal.dto.outputDTO;

import com.stayFinder.proyectoFinal.entity.enums.EstadoReserva;
import com.stayFinder.proyectoFinal.entity.enums.TipoReserva;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Respuesta con los datos de una reserva")
public record ReservaResponseDTO(

        @Schema(description = "ID de la reserva", example = "10")
        Long id,

        @Schema(description = "ID del usuario que realizó la reserva", example = "5")
        Long usuarioId,

        @Schema(description = "ID del alojamiento reservado", example = "12")
        Long alojamientoId,

        @Schema(description = "Nombre del alojamiento reservado", example = "Casa en la Playa")
        String alojamientoNombre,

        @Schema(description = "URL de imagen del alojamiento", example = "https://...")
        String alojamientoImagen,

        @Schema(description = "Fecha de inicio", example = "2025-12-20")
        LocalDate fechaInicio,

        @Schema(description = "Fecha de fin", example = "2025-12-25")
        LocalDate fechaFin,

        @Schema(description = "Cantidad de huéspedes", example = "3")
        Integer numeroHuespedes,

        @Schema(description = "Precio total de la reserva", example = "500000.0")
        Double precioTotal,

        @Schema(description = "Estado de la reserva", example = "CONFIRMADA")
        EstadoReserva estado,

        @Schema(description = "Tipo de reserva", example = "SENCILLA")
        TipoReserva tipoReserva
) {}

