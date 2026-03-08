package com.stayFinder.proyectoFinal.services.alojamientoService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.AlojamientoRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.BloqueoDisponibilidadRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.AlojamientoResponseDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.BloqueoDisponibilidadResponseDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ImagenAlojamientoResponseDTO;
import com.stayFinder.proyectoFinal.entity.Alojamiento;
import com.stayFinder.proyectoFinal.entity.BloqueoDisponibilidad;
import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.entity.enums.EstadoAlojamiento;
import com.stayFinder.proyectoFinal.entity.enums.EstadoReserva;
import com.stayFinder.proyectoFinal.entity.enums.EstadoSolicitudPublicacion;
import com.stayFinder.proyectoFinal.entity.enums.Role;
import com.stayFinder.proyectoFinal.repository.AlojamientoRepository;
import com.stayFinder.proyectoFinal.repository.BloqueoDisponibilidadRepository;
import com.stayFinder.proyectoFinal.repository.ReservaRepository;
import com.stayFinder.proyectoFinal.repository.SolicitudPublicacionRepository;
import com.stayFinder.proyectoFinal.repository.UsuarioRepository;
import com.stayFinder.proyectoFinal.services.alojamientoService.interfaces.AlojamientoServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AlojamientoServiceImpl implements AlojamientoServiceInterface {

    private final AlojamientoRepository alojamientoRepo;
    private final UsuarioRepository usuarioRepo;
    private final ReservaRepository reservaRepo;
    private final SolicitudPublicacionRepository solicitudRepo;
    private final BloqueoDisponibilidadRepository bloqueoRepo;

    @Override
    public AlojamientoResponseDTO crear(AlojamientoRequestDTO req, Long ownerId) {
        Usuario owner = usuarioRepo.findByUsuarioId(ownerId)
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
                .estado(EstadoAlojamiento.BORRADOR)
                .build();

        alojamientoRepo.save(alojamiento);

        AlojamientoResponseDTO dto = new AlojamientoResponseDTO();
        dto.setId(alojamiento.getId());
        dto.setNombre(alojamiento.getNombre());
        dto.setDireccion(alojamiento.getDireccion());
        dto.setPrecio(alojamiento.getPrecio());
        dto.setDescripcion(alojamiento.getDescripcion());
        dto.setCapacidadMaxima(alojamiento.getCapacidadMaxima());
        dto.setOwnerId(alojamiento.getOwner().getUsuarioId());
        dto.setEstado(determinarEstado(alojamiento));
        return dto;
    }

    @Override
    public AlojamientoResponseDTO editar(Long alojamientoId, AlojamientoRequestDTO req, Long ownerId) {
        Alojamiento alojamiento = alojamientoRepo.findById(alojamientoId)
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));

        if (alojamiento.isEliminado()) {
            throw new RuntimeException("El alojamiento fue eliminado");
        }

        if (!alojamiento.getOwner().getUsuarioId().equals(ownerId)) {
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
        dto.setCapacidadMaxima(alojamiento.getCapacidadMaxima());
        dto.setOwnerId(alojamiento.getOwner().getUsuarioId());
        dto.setEstado(determinarEstado(alojamiento));
        return dto;
    }

    public List<AlojamientoResponseDTO> obtenerAlojamientosDeOwner(Long ownerId) {
        Usuario owner = usuarioRepo.findByUsuarioId(ownerId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        return alojamientoRepo.findByOwnerIdAndEliminadoFalse(owner.getId()).stream()
                .map(a -> {
                    AlojamientoResponseDTO dto = new AlojamientoResponseDTO();
                    dto.setId(a.getId());
                    dto.setNombre(a.getNombre());
                    dto.setDireccion(a.getDireccion());
                    dto.setPrecio(a.getPrecio());
                    dto.setDescripcion(a.getDescripcion());
                    dto.setCapacidadMaxima(a.getCapacidadMaxima());
                    dto.setOwnerId(a.getOwner().getUsuarioId());
                    dto.setEstado(determinarEstado(a));

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
        // Usa el filtro real de EstadoAlojamiento.ACTIVO
        return alojamientoRepo.findByEstadoAndEliminadoFalse(EstadoAlojamiento.ACTIVO).stream()
                .map(a -> {
                    AlojamientoResponseDTO dto = new AlojamientoResponseDTO();
                    dto.setId(a.getId());
                    dto.setNombre(a.getNombre());
                    dto.setDireccion(a.getDireccion());
                    dto.setPrecio(a.getPrecio());
                    dto.setDescripcion(a.getDescripcion());
                    dto.setCapacidadMaxima(a.getCapacidadMaxima());
                    dto.setOwnerId(a.getOwner().getUsuarioId());
                    dto.setEstado(determinarEstado(a));

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

        if (!alojamiento.getOwner().getUsuarioId().equals(ownerId)) {
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
        dto.setCapacidadMaxima(alojamiento.getCapacidadMaxima());
        dto.setOwnerId(alojamiento.getOwner().getUsuarioId());
        dto.setEstado(determinarEstado(alojamiento));

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

    @Override
    public AlojamientoResponseDTO cambiarEstado(Long alojamientoId, EstadoAlojamiento nuevoEstado, Long ownerId) {
        Alojamiento alojamiento = alojamientoRepo.findById(alojamientoId)
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));
                
        if (!alojamiento.getOwner().getUsuarioId().equals(ownerId) && !alojamiento.getOwner().getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("No tienes permisos para cambiar el estado de este alojamiento");
        }
        
        // Regla: No se puede Activar si no está aprobado/publicado 
        // (ya tiene una validación de estado: tiene que estar ACTIVO para mostrarse, 
        // o si es ADMIN puede forzarlo). Si quieren desactivarlo/activarlo (boton) debe usar esto.
        
        alojamiento.setEstado(nuevoEstado);
        alojamientoRepo.save(alojamiento);
        
        return obtenerPorId(alojamientoId);
    }

    @Override
    public BloqueoDisponibilidadResponseDTO bloquearFechas(Long alojamientoId, BloqueoDisponibilidadRequestDTO req, Long ownerId) {
        Alojamiento alojamiento = alojamientoRepo.findById(alojamientoId)
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));
                
        if (!alojamiento.getOwner().getUsuarioId().equals(ownerId)) {
            throw new RuntimeException("No puedes bloquear fechas de un alojamiento que no es tuyo");
        }
        
        BloqueoDisponibilidad bloqueo = BloqueoDisponibilidad.builder()
                .alojamiento(alojamiento)
                .fechaInicio(req.fechaInicio())
                .fechaFin(req.fechaFin())
                .motivo(req.motivo())
                .build();
                
        bloqueo = bloqueoRepo.save(bloqueo);
        
        return new BloqueoDisponibilidadResponseDTO(
                bloqueo.getId(),
                bloqueo.getAlojamiento().getId(),
                bloqueo.getFechaInicio(),
                bloqueo.getFechaFin(),
                bloqueo.getMotivo()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloqueoDisponibilidadResponseDTO> obtenerBloqueos(Long alojamientoId) {
        List<BloqueoDisponibilidad> bloqueos = bloqueoRepo.findByAlojamientoId(alojamientoId);
        return bloqueos.stream().map(b -> new BloqueoDisponibilidadResponseDTO(
                b.getId(),
                b.getAlojamiento().getId(),
                b.getFechaInicio(),
                b.getFechaFin(),
                b.getMotivo()
        )).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticasDashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActivos", alojamientoRepo.countByEstadoAndEliminadoFalse(EstadoAlojamiento.ACTIVO));
        stats.put("creadosUltimos30Dias", alojamientoRepo.countByCreatedAtAfter(LocalDateTime.now().minusDays(30)));
        stats.put("cantidadPorEstado", alojamientoRepo.findCountByEstado());
        
        // Nuevas métricas
        stats.put("eliminadosTotales", alojamientoRepo.countByEliminadoTrue());
        stats.put("editadosTotales", alojamientoRepo.countEditedAlojamientos());
        
        return stats;
    }

    private String determinarEstado(Alojamiento alojamiento) {
        return alojamiento.getEstado() != null ? alojamiento.getEstado().name() : EstadoAlojamiento.BORRADOR.name();
    }
}
