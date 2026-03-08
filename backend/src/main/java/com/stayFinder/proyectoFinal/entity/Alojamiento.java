package com.stayFinder.proyectoFinal.entity;

import com.stayFinder.proyectoFinal.entity.base.Auditable;
import com.stayFinder.proyectoFinal.entity.enums.EstadoAlojamiento;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "alojamientos")
public class Alojamiento extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String direccion;
    private Double precio;
    private String descripcion;

    private Integer capacidadMaxima;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Usuario owner;
    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImagenAlojamiento> imagenes = new ArrayList<>();

    @OneToOne(mappedBy = "alojamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private Publicacion publicacion;
    @Builder.Default
    @Column(nullable = false)
    private boolean eliminado = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private EstadoAlojamiento estado = EstadoAlojamiento.BORRADOR;

}
