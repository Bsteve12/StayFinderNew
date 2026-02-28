package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
}
