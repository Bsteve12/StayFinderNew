package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.entity.Publicacion;
import com.stayFinder.proyectoFinal.entity.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    List<Publicacion> findByEstado(EstadoSolicitudPublicacion estado);
}

