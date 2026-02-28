package com.stayFinder.proyectoFinal.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlojamientoServicioId implements Serializable { //clave compuesta
    private Long alojamientoId;
    private Long servicioId;
}
