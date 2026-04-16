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
    @org.springframework.data.jpa.repository.Query("SELECT r FROM Reserva r WHERE r.alojamiento.id = :alojamientoId AND r.estado = :estado")
    List<Reserva> findByAlojamientoIdAndEstado(@org.springframework.data.repository.query.Param("alojamientoId") Long alojamientoId, @org.springframework.data.repository.query.Param("estado") EstadoReserva estado);

    // Validar existencia de reserva de un usuario sobre un alojamiento de un owner con un estado específico
    boolean existsByUsuarioAndAlojamientoOwnerAndEstado(Usuario usuario, Usuario owner, EstadoReserva estado);

    // Consulta de solapamiento de fechas para disponibilidad
    @org.springframework.data.jpa.repository.Query("SELECT r FROM Reserva r WHERE r.alojamiento.id = :alojamientoId " +
            "AND r.estado IN :estados " +
            "AND r.fechaInicio < :fechaFin AND r.fechaFin > :fechaInicio")
    List<Reserva> findOverlappingReservations(
            @org.springframework.data.repository.query.Param("alojamientoId") Long alojamientoId,
            @org.springframework.data.repository.query.Param("fechaInicio") java.time.LocalDate fechaInicio,
            @org.springframework.data.repository.query.Param("fechaFin") java.time.LocalDate fechaFin,
            @org.springframework.data.repository.query.Param("estados") List<EstadoReserva> estados);
}
