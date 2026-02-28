package com.stayFinder.proyectoFinal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "imagenes_alojamiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenAlojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @ManyToOne
    @JoinColumn(name = "alojamiento_id")
    private Alojamiento alojamiento;
}
