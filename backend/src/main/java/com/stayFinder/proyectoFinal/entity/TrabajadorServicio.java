package com.stayFinder.proyectoFinal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trabajadores_servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrabajadorServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String tipo; // "ASEO", "CHEF"

    @ManyToOne
    @JoinColumn(name = "servicio_id")
    private Servicio servicio;
}
