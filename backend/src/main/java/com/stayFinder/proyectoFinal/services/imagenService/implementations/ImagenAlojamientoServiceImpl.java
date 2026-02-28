package com.stayFinder.proyectoFinal.services.imagenService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.ImagenAlojamientoRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ImagenAlojamientoResponseDTO;
import com.stayFinder.proyectoFinal.entity.Alojamiento;
import com.stayFinder.proyectoFinal.entity.ImagenAlojamiento;
import com.stayFinder.proyectoFinal.mapper.ImagenAlojamientoMapper;
import com.stayFinder.proyectoFinal.repository.AlojamientoRepository;
import com.stayFinder.proyectoFinal.repository.ImagenAlojamientoRepository;
import com.stayFinder.proyectoFinal.services.imagenService.interfaces.ImagenAlojamientoServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImagenAlojamientoServiceImpl implements ImagenAlojamientoServiceInterface {

    private final ImagenAlojamientoRepository imagenRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final ImagenAlojamientoMapper mapper;

    @Override
    public ImagenAlojamientoResponseDTO subirImagen(ImagenAlojamientoRequestDTO dto) throws Exception {
        Alojamiento alojamiento = alojamientoRepository.findById(dto.getAlojamientoId())
                .orElseThrow(() -> new Exception("Alojamiento no encontrado"));

        ImagenAlojamiento imagen = mapper.toEntity(dto);
        imagen.setAlojamiento(alojamiento);

        ImagenAlojamiento saved = imagenRepository.save(imagen);
        return mapper.toDto(saved);
    }

    @Override
    public ImagenAlojamientoResponseDTO obtenerImagen(Long id) throws Exception {
        ImagenAlojamiento imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new Exception("Imagen no encontrada"));
        return mapper.toDto(imagen);
    }

    @Override
    public List<ImagenAlojamientoResponseDTO> listarPorAlojamiento(Long alojamientoId) throws Exception {
        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new Exception("Alojamiento no encontrado"));

        return alojamiento.getImagenes().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void eliminarImagen(Long id) throws Exception {
        ImagenAlojamiento imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new Exception("Imagen no encontrada"));
        imagenRepository.delete(imagen);
    }
}
