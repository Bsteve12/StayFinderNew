package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.entity.Comentario;
import com.stayFinder.proyectoFinal.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByAlojamientoIdOrderByFechaCreacionDesc(Long alojamientoId);

    Optional<Comentario> findByReserva(Reserva reserva);

    List<Comentario> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
}
