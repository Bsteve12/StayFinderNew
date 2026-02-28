package com.stayFinder.proyectoFinal.entity;

import com.stayFinder.proyectoFinal.entity.enums.EstadoReserva;

import com.stayFinder.proyectoFinal.entity.enums.TipoReserva;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "alojamiento_id")
    private Alojamiento alojamiento;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer numeroHuespedes;
    private Double precioTotal;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    private TipoReserva tipoReserva;
}

