package com.stayFinder.proyectoFinal.services.reservaService.interfaces;

import com.stayFinder.proyectoFinal.dto.inputDTO.*;
import com.stayFinder.proyectoFinal.dto.outputDTO.ReservaResponseDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ReservaHistorialResponseDTO;

import java.util.List;
import java.util.Optional;

public interface ReservaServiceInterface {

    // Crear una nueva reserva
    ReservaResponseDTO createReserva(ReservaRequestDTO dto, Long userId) throws Exception;


    // Cancelar reserva (con motivo opcional)
    void cancelarReserva(CancelarReservaRequestDTO dto, Long userId) throws Exception;

    // =============================
    // ELIMINAR RESERVA (cancelar)
    // =============================
    void deleteReserva(Long id) throws Exception;

    // Actualizar datos de una reserva
    ReservaResponseDTO actualizarReserva(ActualizarReservaRequestDTO dto, Long userId) throws Exception;

    // Confirmar reserva
    void confirmarReserva(Long id, Long userId) throws Exception;

    // Obtener reservas por usuario
    List<ReservaResponseDTO> obtenerReservasUsuario(Long usuarioId) throws Exception;

    // Métodos utilitarios
    Optional<ReservaResponseDTO> findById(Long id);
    ReservaResponseDTO save(ReservaRequestDTO dto) throws Exception;
    void deleteById(Long id, Long userId) throws Exception;
    List<ReservaHistorialResponseDTO> historialReservasUsuario(Long usuarioId, HistorialReservasRequestDTO filtros) throws Exception;

    List<ReservaHistorialResponseDTO> historialReservasAnfitrion(Long ownerId, HistorialReservasRequestDTO filtros) throws Exception;

    ReservaResponseDTO createReservaBasica(CreateReservaRequestDTO dto, Long usuarioId) throws Exception;

}

