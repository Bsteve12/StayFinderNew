package com.stayFinder.proyectoFinal.services.alojamientoService.interfaces;

import com.stayFinder.proyectoFinal.dto.inputDTO.AlojamientoRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.BloqueoDisponibilidadRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.AlojamientoResponseDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.BloqueoDisponibilidadResponseDTO;
import com.stayFinder.proyectoFinal.entity.enums.EstadoAlojamiento;

import java.util.List;
import java.util.Map;

public interface AlojamientoServiceInterface{
    AlojamientoResponseDTO crear(AlojamientoRequestDTO req, Long ownerId);
    AlojamientoResponseDTO editar(Long alojamientoId, AlojamientoRequestDTO req, Long ownerId);
    void eliminar(Long alojamientoId, Long ownerId);
    AlojamientoResponseDTO obtenerPorId(Long id);
    List<AlojamientoResponseDTO> listarAlojamientosActivos();
    List<AlojamientoResponseDTO> obtenerAlojamientosDeOwner(Long ownerId);

    // RF-11 y Flujo de Negocio
    AlojamientoResponseDTO cambiarEstado(Long alojamientoId, EstadoAlojamiento nuevoEstado, Long ownerId);
    BloqueoDisponibilidadResponseDTO bloquearFechas(Long alojamientoId, BloqueoDisponibilidadRequestDTO req, Long ownerId);
    List<BloqueoDisponibilidadResponseDTO> obtenerBloqueos(Long alojamientoId);
    
    // Dashboard Stats
    Map<String, Object> obtenerEstadisticasDashboard();
}
