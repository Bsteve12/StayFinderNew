package com.stayFinder.proyectoFinal.entity;

import com.stayFinder.proyectoFinal.entity.enums.EstadoSolicitudPublicacion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "publicaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitudPublicacion estado;

    // Relación con Usuario (quien crea la publicación)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relación con Alojamiento
    @OneToOne
    @JoinColumn(name = "alojamiento_id", referencedColumnName = "id")
    private Alojamiento alojamiento;
}
