package com.stayFinder.proyectoFinal.services.servicioService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.ServicioRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ServicioResponseDTO;
import com.stayFinder.proyectoFinal.entity.Servicio;
import com.stayFinder.proyectoFinal.mapper.ServicioMapper;
import com.stayFinder.proyectoFinal.repository.ServicioRepository;
import com.stayFinder.proyectoFinal.services.servicioService.interfaces.ServicioServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicioServiceImpl implements ServicioServiceInterface {

    private final ServicioRepository servicioRepo;
    private final ServicioMapper mapper;

    @Override
    public ServicioResponseDTO crear(ServicioRequestDTO dto) {
        if (servicioRepo.existsByNombre(dto.nombre())) {
            throw new RuntimeException("Ya existe un servicio con ese nombre");
        }
        Servicio servicio = mapper.toEntity(dto);
        return mapper.toDto(servicioRepo.save(servicio));
    }

    @Override
    public ServicioResponseDTO editar(Long id, ServicioRequestDTO dto) {
        Servicio servicio = servicioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        servicio.setNombre(dto.nombre());
        servicio.setPrecio(dto.precio());
        return mapper.toDto(servicioRepo.save(servicio));
    }

    @Override
    public void eliminar(Long id) {
        Servicio servicio = servicioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        servicioRepo.delete(servicio);
    }

    @Override
    public List<ServicioResponseDTO> listar() {
        return servicioRepo.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public ServicioResponseDTO obtenerPorId(Long id) {
        Servicio servicio = servicioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        return mapper.toDto(servicio);
    }
}
