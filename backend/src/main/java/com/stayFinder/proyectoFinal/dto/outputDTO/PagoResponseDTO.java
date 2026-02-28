package com.stayFinder.proyectoFinal.dto.outputDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta con la información de un pago realizado")
public class PagoResponseDTO {
    @Schema(description = "ID del pago", example = "2001")
    private Long id;

    @Schema(description = "ID de la reserva asociada al pago", example = "50")
    private Long reservaId;

    @Schema(description = "Método de pago utilizado", example = "Tarjeta de Crédito")
    private String metodoPago;

    @Schema(description = "Monto pagado", example = "450000.0")
    private Double monto;

    @Schema(description = "Estado del pago", example = "PAGADO")
    private String estado;

    @Schema(description = "Fecha y hora en que se registró el pago", example = "2025-09-12T20:10:00")
    private LocalDateTime fechaPago;
}
