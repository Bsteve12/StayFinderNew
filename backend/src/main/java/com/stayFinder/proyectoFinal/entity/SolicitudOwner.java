package com.stayFinder.proyectoFinal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.stayFinder.proyectoFinal.entity.enums.EstadoSolicitud;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity

@Table(name = "solicitudes_owner")
public class SolicitudOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Cliente que solicita convertirse en Owner

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    private String comentario;

    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    private LocalDateTime fechaRevision;

    //  ruta del documento subido (PDF)
    private String documentoRuta;
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Usuario adminRevisor; // Admin que revisa la solicitud


}
