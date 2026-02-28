package com.stayFinder.proyectoFinal.services.imagenService.interfaces;

import com.stayFinder.proyectoFinal.dto.inputDTO.ImagenAlojamientoRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ImagenAlojamientoResponseDTO;

import java.util.List;

public interface ImagenAlojamientoServiceInterface {

    ImagenAlojamientoResponseDTO subirImagen(ImagenAlojamientoRequestDTO dto) throws Exception;

    ImagenAlojamientoResponseDTO obtenerImagen(Long id) throws Exception;

    List<ImagenAlojamientoResponseDTO> listarPorAlojamiento(Long alojamientoId) throws Exception;

    void eliminarImagen(Long id) throws Exception;
}
