package com.stayFinder.proyectoFinal.dto.outputDTO;

import com.stayFinder.proyectoFinal.entity.enums.EstadoReserva;
import com.stayFinder.proyectoFinal.entity.enums.TipoReserva;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con el historial de reservas de un usuario o alojamiento")
public class ReservaHistorialResponseDTO {

    @Schema(description = "ID de la reserva", example = "101")
    private Long reservaId;

    @Schema(description = "ID del alojamiento reservado", example = "45")
    private Long alojamientoId;

    @Schema(description = "Nombre del alojamiento reservado", example = "Hotel Sol y Mar")
    private String alojamientoNombre;

    @Schema(description = "ID del usuario que realizó la reserva", example = "3")
    private Long usuarioId;

    @Schema(description = "Nombre del usuario que realizó la reserva", example = "Brandon Ceballos")
    private String usuarioNombre;

    @Schema(description = "Fecha de inicio de la reserva", example = "2025-07-15")
    private LocalDate fechaInicio;

    @Schema(description = "Fecha de fin de la reserva", example = "2025-07-20")
    private LocalDate fechaFin;

    @Schema(description = "Número de huéspedes incluidos en la reserva", example = "4")
    private int numeroHuespedes;

    @Schema(description = "Precio total de la reserva", example = "550000.0")
    private double precioTotal;

    @Schema(description = "Estado actual de la reserva", example = "CONFIRMADA")
    private EstadoReserva estado;

    @Schema(description = "Tipo de reserva realizada", example = "ONLINE")
    private TipoReserva tipoReserva;
}
