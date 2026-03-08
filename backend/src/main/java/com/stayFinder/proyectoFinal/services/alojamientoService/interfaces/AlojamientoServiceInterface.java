package com.stayFinder.proyectoFinal.services.alojamientoService.interfaces;

import com.stayFinder.proyectoFinal.dto.inputDTO.AlojamientoRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.BloqueoDisponibilidadRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.AlojamientoResponseDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.BloqueoDisponibilidadResponseDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.DisponibilidadDTO;
import com.stayFinder.proyectoFinal.entity.enums.EstadoAlojamiento;

import java.util.List;

public interface AlojamientoServiceInterface {
    AlojamientoResponseDTO crear(AlojamientoRequestDTO req, Long ownerId);

    AlojamientoResponseDTO editar(Long alojamientoId, AlojamientoRequestDTO req, Long ownerId);

    void eliminar(Long alojamientoId, Long ownerId);

    AlojamientoResponseDTO obtenerPorId(Long id);

    List<AlojamientoResponseDTO> listarAlojamientosActivos();

    List<AlojamientoResponseDTO> obtenerAlojamientosDeOwner(Long ownerId);

    // Nuevos métodos para Gestión de Estados y Calendario
    void cambiarEstado(Long alojamientoId, EstadoAlojamiento nuevoEstado, Long adminId);

    List<DisponibilidadDTO> obtenerCalendario(Long alojamientoId);

    BloqueoDisponibilidadResponseDTO agregarBloqueo(BloqueoDisponibilidadRequestDTO request, Long ownerId);

    void eliminarBloqueo(Long bloqueoId, Long ownerId);
}
