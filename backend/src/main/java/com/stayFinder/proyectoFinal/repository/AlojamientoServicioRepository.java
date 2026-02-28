package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.entity.AlojamientoServicio;
import com.stayFinder.proyectoFinal.entity.AlojamientoServicioId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlojamientoServicioRepository extends JpaRepository<AlojamientoServicio, AlojamientoServicioId> {}
