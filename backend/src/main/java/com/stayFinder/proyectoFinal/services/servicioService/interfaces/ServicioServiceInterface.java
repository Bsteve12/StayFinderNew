package com.stayFinder.proyectoFinal.services.servicioService.interfaces;

import com.stayFinder.proyectoFinal.dto.inputDTO.ServicioRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ServicioResponseDTO;

import java.util.List;

public interface ServicioServiceInterface {
    ServicioResponseDTO crear(ServicioRequestDTO dto);
    ServicioResponseDTO editar(Long id, ServicioRequestDTO dto);
    void eliminar(Long id);
    List<ServicioResponseDTO> listar();
    ServicioResponseDTO obtenerPorId(Long id);
}
