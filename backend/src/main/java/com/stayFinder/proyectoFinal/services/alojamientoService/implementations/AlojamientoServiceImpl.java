package com.stayFinder.proyectoFinal.services.alojamientoService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.AlojamientoRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.AlojamientoResponseDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ImagenAlojamientoResponseDTO;
import com.stayFinder.proyectoFinal.entity.Alojamiento;
import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.entity.enums.EstadoReserva;
import com.stayFinder.proyectoFinal.entity.enums.Role;
import com.stayFinder.proyectoFinal.repository.AlojamientoRepository;
import com.stayFinder.proyectoFinal.repository.ReservaRepository;
import com.stayFinder.proyectoFinal.repository.UsuarioRepository;
import com.stayFinder.proyectoFinal.services.alojamientoService.interfaces.AlojamientoServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlojamientoServiceImpl implements AlojamientoServiceInterface {

    private final AlojamientoRepository alojamientoRepo;
    private final UsuarioRepository usuarioRepo;
    private final ReservaRepository reservaRepo;

    @Override
    public AlojamientoResponseDTO crear(AlojamientoRequestDTO req, Long ownerId) {
        Usuario owner = usuarioRepo.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validación de roles permitidos
        if (!(owner.getRole().equals(Role.OWNER) || owner.getRole().equals(Role.ADMIN))) {
            throw new RuntimeException("Solo OWNERS o ADMIN pueden crear alojamientos");
        }

        Alojamiento alojamiento = Alojamiento.builder()
                .nombre(req.nombre())
                .direccion(req.direccion())
                .precio(req.precio())
                .descripcion(req.descripcion())
                .capacidadMaxima(req.capacidadMaxima())
                .owner(owner)
                .eliminado(false)
                .build();

        alojamientoRepo.save(alojamiento);

        AlojamientoResponseDTO dto = new AlojamientoResponseDTO();
        dto.setId(alojamiento.getId());
        dto.setNombre(alojamiento.getNombre());
        dto.setDireccion(alojamiento.getDireccion());
        dto.setPrecio(alojamiento.getPrecio());
        dto.setDescripcion(alojamiento.getDescripcion());
        dto.setOwnerId(alojamiento.getOwner().getId());
        return dto;
    }

    @Override
    public AlojamientoResponseDTO editar(Long alojamientoId, AlojamientoRequestDTO req, Long ownerId) {
        Alojamiento alojamiento = alojamientoRepo.findById(alojamientoId)
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));

        if (alojamiento.isEliminado()) {
            throw new RuntimeException("El alojamiento fue eliminado");
        }

        if (!alojamiento.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("No puedes editar un alojamiento que no es tuyo");
        }

        alojamiento.setNombre(req.nombre());
        alojamiento.setDireccion(req.direccion());
        alojamiento.setPrecio(req.precio());
        alojamiento.setDescripcion(req.descripcion());
        alojamiento.setCapacidadMaxima(req.capacidadMaxima());

        alojamientoRepo.save(alojamiento);

        AlojamientoResponseDTO dto = new AlojamientoResponseDTO();
        dto.setId(alojamiento.getId());
        dto.setNombre(alojamiento.getNombre());
        dto.setDireccion(alojamiento.getDireccion());
        dto.setPrecio(alojamiento.getPrecio());
        dto.setDescripcion(alojamiento.getDescripcion());
        dto.setOwnerId(alojamiento.getOwner().getId());
        return dto;
    }

    public List<AlojamientoResponseDTO> obtenerAlojamientosDeOwner(Long ownerId) {
        return alojamientoRepo.findByOwnerIdAndEliminadoFalse(ownerId).stream()
                .map(a -> {
                    AlojamientoResponseDTO dto = new AlojamientoResponseDTO();
                    dto.setId(a.getId());
                    dto.setNombre(a.getNombre());
                    dto.setDireccion(a.getDireccion());
                    dto.setPrecio(a.getPrecio());
                    dto.setDescripcion(a.getDescripcion());
                    dto.setOwnerId(a.getOwner().getId());

                    // Mapeamos las imágenes si existen
                    if (a.getImagenes() != null && !a.getImagenes().isEmpty()) {
                        List<ImagenAlojamientoResponseDTO> imagenes = a.getImagenes().stream()
                                .map(img -> {
                                    ImagenAlojamientoResponseDTO imgDto = new ImagenAlojamientoResponseDTO();
                                    imgDto.setId(img.getId());
                                    imgDto.setUrl(img.getUrl());
                                    imgDto.setAlojamientoId(a.getId());
                                    return imgDto;
                                })
                                .toList();
                        dto.setImagenes(imagenes);
                    }
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlojamientoResponseDTO> listarAlojamientosActivos() {
        return alojamientoRepo.findByEliminadoFalse().stream()
                .map(a -> {
                    AlojamientoResponseDTO dto = new AlojamientoResponseDTO();
                    dto.setId(a.getId());
                    dto.setNombre(a.getNombre());
                    dto.setDireccion(a.getDireccion());
                    dto.setPrecio(a.getPrecio());
                    dto.setDescripcion(a.getDescripcion());
                    dto.setOwnerId(a.getOwner().getId());

                    // 🔹 Mapeamos las imágenes asociadas al alojamiento
                    if (a.getImagenes() != null && !a.getImagenes().isEmpty()) {
                        List<ImagenAlojamientoResponseDTO> imagenes = a.getImagenes().stream()
                                .map(img -> {
                                    ImagenAlojamientoResponseDTO imgDto = new ImagenAlojamientoResponseDTO();
                                    imgDto.setId(img.getId());
                                    imgDto.setUrl(img.getUrl());
                                    imgDto.setAlojamientoId(a.getId());
                                    return imgDto;
                                })
                                .toList();
                        dto.setImagenes(imagenes);
                    }

                    return dto;
                })
                .toList();
    }


    @Override
    public void eliminar(Long alojamientoId, Long ownerId) {
        Alojamiento alojamiento = alojamientoRepo.findById(alojamientoId)
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));

        if (!alojamiento.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("No puedes eliminar un alojamiento que no es tuyo");
        }

        // Validar reservas futuras confirmadas antes de marcar como eliminado
        boolean tieneReservasFuturas = !reservaRepo
                .findByAlojamientoIdAndEstado(alojamientoId, EstadoReserva.CONFIRMADA)
                .isEmpty();

        if (tieneReservasFuturas) {
            throw new RuntimeException("No puedes eliminar un alojamiento con reservas confirmadas futuras");
        }

        // Soft delete
        alojamiento.setEliminado(true);
        alojamientoRepo.save(alojamiento);
    }

    @Override
    @Transactional(readOnly = true)
    public AlojamientoResponseDTO obtenerPorId(Long id) {
        Alojamiento alojamiento = alojamientoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));

        if (alojamiento.isEliminado()) {
            throw new RuntimeException("Este alojamiento está eliminado");
        }

        AlojamientoResponseDTO dto = new AlojamientoResponseDTO();
        dto.setId(alojamiento.getId());
        dto.setNombre(alojamiento.getNombre());
        dto.setDireccion(alojamiento.getDireccion());
        dto.setPrecio(alojamiento.getPrecio());
        dto.setDescripcion(alojamiento.getDescripcion());
        dto.setOwnerId(alojamiento.getOwner().getId());

        // 👇 Mapeamos las imágenes
        if (alojamiento.getImagenes() != null && !alojamiento.getImagenes().isEmpty()) {
            List<ImagenAlojamientoResponseDTO> imagenes = alojamiento.getImagenes().stream()
                    .map(img -> {
                        ImagenAlojamientoResponseDTO imgDto = new ImagenAlojamientoResponseDTO();
                        imgDto.setId(img.getId());
                        imgDto.setUrl(img.getUrl());
                        imgDto.setAlojamientoId(alojamiento.getId());
                        return imgDto;
                    })
                    .toList();
            dto.setImagenes(imagenes);
        }

        return dto;
    }
}
