package com.stayFinder.proyectoFinal.services.alojamientoService.interfaces;



import com.stayFinder.proyectoFinal.dto.inputDTO.AlojamientoRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.AlojamientoResponseDTO;

import java.util.List;

public interface AlojamientoServiceInterface{
    AlojamientoResponseDTO crear(AlojamientoRequestDTO req, Long ownerId);
    AlojamientoResponseDTO editar(Long alojamientoId, AlojamientoRequestDTO req, Long ownerId);
    void eliminar(Long alojamientoId, Long ownerId);
    AlojamientoResponseDTO obtenerPorId(Long id);
    List<AlojamientoResponseDTO> listarAlojamientosActivos();
    List<AlojamientoResponseDTO> obtenerAlojamientosDeOwner(Long ownerId);
}
