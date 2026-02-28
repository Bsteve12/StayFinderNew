package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.entity.SolicitudOwner;
import com.stayFinder.proyectoFinal.entity.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudOwnerRepository extends JpaRepository<SolicitudOwner, Long> {
    List<SolicitudOwner> findByEstado(EstadoSolicitud estado);
}
