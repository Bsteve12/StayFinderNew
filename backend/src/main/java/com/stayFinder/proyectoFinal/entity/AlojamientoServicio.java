package com.stayFinder.proyectoFinal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alojamiento_servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlojamientoServicio {

    @EmbeddedId
    private AlojamientoServicioId id;

    @ManyToOne
    @MapsId("alojamientoId")
    @JoinColumn(name = "alojamiento_id")
    private Alojamiento alojamiento;

    @ManyToOne
    @MapsId("servicioId")
    @JoinColumn(name = "servicio_id")
    private Servicio servicio;
}
