package com.stayFinder.proyectoFinal.services.pagoService.interfaces;

import com.stayFinder.proyectoFinal.dto.inputDTO.PagoRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.PagoResponseDTO;

import java.util.List;

public interface PagoServiceInterface {
    PagoResponseDTO registrarPago(PagoRequestDTO dto);
    List<PagoResponseDTO> listarPagos();
    PagoResponseDTO obtenerPagoPorId(Long id);
}
