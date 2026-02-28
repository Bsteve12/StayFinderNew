package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.entity.Reserva;
import com.stayFinder.proyectoFinal.dto.outputDTO.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportesRepository extends JpaRepository<Reserva, Long> {

    @Query("SELECT new com.stayFinder.proyectoFinal.dto.outputDTO.ReservasPorUsuarioResponseDTO(u.nombre, COUNT(r)) " +
            "FROM Reserva r JOIN r.usuario u " +
            "GROUP BY u.nombre")
    List<ReservasPorUsuarioResponseDTO> obtenerReservasPorUsuario();

    @Query("SELECT new com.stayFinder.proyectoFinal.dto.outputDTO.IngresosPorAlojamientoResponseDTO(a.nombre, SUM(r.precioTotal)) " +
            "FROM Reserva r JOIN r.alojamiento a " +
            "GROUP BY a.nombre")
    List<IngresosPorAlojamientoResponseDTO> obtenerIngresosPorAlojamiento();

    @Query("SELECT new com.stayFinder.proyectoFinal.dto.outputDTO.PublicacionesPendientesResponseDTO(p.titulo, p.estado) " +
            "FROM Publicacion p " +
            "WHERE p.estado = com.stayFinder.proyectoFinal.entity.enums.EstadoSolicitudPublicacion.PENDIENTE")
    List<PublicacionesPendientesResponseDTO> obtenerPublicacionesPendientes();

    @Query("SELECT new com.stayFinder.proyectoFinal.dto.outputDTO.UsuariosActivosResponseDTO(u.nombre, u.email) " +
            "FROM Reserva r JOIN r.usuario u " +
            "GROUP BY u.id, u.nombre, u.email HAVING COUNT(r) > 0")
    List<UsuariosActivosResponseDTO> obtenerUsuariosActivos();

    @Query("SELECT new com.stayFinder.proyectoFinal.dto.outputDTO.FavoritosPorUsuarioResponseDTO(u.nombre, a.nombre) " +
            "FROM Favorite f JOIN f.usuario u JOIN f.alojamiento a")
    List<FavoritosPorUsuarioResponseDTO> obtenerFavoritosPorUsuario();
}
