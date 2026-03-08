package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.entity.BloqueoDisponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloqueoDisponibilidadRepository extends JpaRepository<BloqueoDisponibilidad, Long> {
    List<BloqueoDisponibilidad> findByAlojamientoId(Long alojamientoId);
}
