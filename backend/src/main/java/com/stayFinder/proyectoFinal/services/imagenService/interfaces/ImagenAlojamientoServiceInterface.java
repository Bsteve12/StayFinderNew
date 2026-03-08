package com.stayFinder.proyectoFinal.services.imagenService.interfaces;

import com.stayFinder.proyectoFinal.dto.inputDTO.ImagenAlojamientoRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ImagenAlojamientoResponseDTO;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ImagenAlojamientoServiceInterface {

    ImagenAlojamientoResponseDTO subirImagen(ImagenAlojamientoRequestDTO dto) throws Exception;

    ImagenAlojamientoResponseDTO obtenerImagen(Long id) throws Exception;

    List<ImagenAlojamientoResponseDTO> subirImagenes(Long alojamientoId, List<MultipartFile> imagenes,
            List<String> nuevasUrls) throws Exception;

    List<ImagenAlojamientoResponseDTO> listarPorAlojamiento(Long alojamientoId) throws Exception;

    void eliminarImagen(Long id) throws Exception;
}
