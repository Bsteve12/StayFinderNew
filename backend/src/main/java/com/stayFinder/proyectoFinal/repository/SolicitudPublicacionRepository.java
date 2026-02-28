package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.entity.SolicitudPublicacion;
import com.stayFinder.proyectoFinal.entity.enums.EstadoSolicitudPublicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudPublicacionRepository extends JpaRepository<SolicitudPublicacion, Long> {
    List<SolicitudPublicacion> findByEstado(EstadoSolicitudPublicacion estado);
}
