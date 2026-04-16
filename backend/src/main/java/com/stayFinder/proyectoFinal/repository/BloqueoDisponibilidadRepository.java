package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.entity.BloqueoDisponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloqueoDisponibilidadRepository extends JpaRepository<BloqueoDisponibilidad, Long> {
    List<BloqueoDisponibilidad> findByAlojamientoId(Long alojamientoId);

    // Consulta de solapamiento de bloqueos manuales
    @org.springframework.data.jpa.repository.Query("SELECT b FROM BloqueoDisponibilidad b WHERE b.alojamiento.id = :alojamientoId " +
            "AND b.fechaInicio < :fechaFin AND b.fechaFin > :fechaInicio")
    List<BloqueoDisponibilidad> findOverlappingBlocks(
            @org.springframework.data.repository.query.Param("alojamientoId") Long alojamientoId,
            @org.springframework.data.repository.query.Param("fechaInicio") java.time.LocalDate fechaInicio,
            @org.springframework.data.repository.query.Param("fechaFin") java.time.LocalDate fechaFin);
}
