package com.stayFinder.proyectoFinal.entity;

import com.stayFinder.proyectoFinal.entity.enums.EstadoSolicitudPublicacion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "solicitudes_publicacion")
public class SolicitudPublicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario que solicita (debe ser OWNER)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Alojamiento que quiere publicar
    @ManyToOne
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitudPublicacion estado = EstadoSolicitudPublicacion.PENDIENTE;

    private String comentario;

    @Column(nullable = false)
    private String titulo;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPublicacion;
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    private LocalDateTime fechaRevision;

    // Admin que revisa la solicitud
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Usuario adminRevisor;
}
