package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.dao.reservaDAO.reservaCustom.ReservaRepositoryCustom;
import com.stayFinder.proyectoFinal.entity.Reserva;
import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.entity.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long>, ReservaRepositoryCustom {

    // Todas las reservas de un usuario
    List<Reserva> findByUsuarioId(Long usuarioId);

    // Todas las reservas de un alojamiento
    List<Reserva> findByAlojamientoId(Long alojamientoId);

    // Reservas confirmadas de un alojamiento
    List<Reserva> findByAlojamientoIdAndEstado(Long alojamientoId, EstadoReserva estado);

    // Validar existencia de reserva de un usuario sobre un alojamiento de un owner con un estado específico
    boolean existsByUsuarioAndAlojamientoOwnerAndEstado(Usuario usuario, Usuario owner, EstadoReserva estado);
}
