package com.stayFinder.proyectoFinal.services.solicitudPublicacionService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.SolicitudPublicacionRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.SolicitudPublicacionRespuestaRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.SolicitudPublicacionResponseDTO;
import com.stayFinder.proyectoFinal.entity.*;
import com.stayFinder.proyectoFinal.entity.enums.EstadoSolicitudPublicacion;
import com.stayFinder.proyectoFinal.entity.enums.Role;
import com.stayFinder.proyectoFinal.mapper.SolicitudPublicacionMapper;
import com.stayFinder.proyectoFinal.repository.PublicacionRepository;
import com.stayFinder.proyectoFinal.repository.SolicitudPublicacionRepository;
import com.stayFinder.proyectoFinal.repository.UsuarioRepository;
import com.stayFinder.proyectoFinal.repository.AlojamientoRepository;
import com.stayFinder.proyectoFinal.services.solicitudPublicacionService.interfaces.SolicitudPublicacionServiceInterface;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudPublicacionServiceImpl implements SolicitudPublicacionServiceInterface {

    private final SolicitudPublicacionRepository solicitudRepo;
    private final UsuarioRepository usuarioRepo;
    private final AlojamientoRepository alojamientoRepo;
    private final PublicacionRepository publicacionRepo;
    private final SolicitudPublicacionMapper mapper;

    public SolicitudPublicacionServiceImpl(SolicitudPublicacionRepository solicitudRepo,
                                           UsuarioRepository usuarioRepo,
                                           AlojamientoRepository alojamientoRepo,
                                           PublicacionRepository publicacionRepo,
                                           SolicitudPublicacionMapper mapper) {
        this.solicitudRepo = solicitudRepo;
        this.usuarioRepo = usuarioRepo;
        this.alojamientoRepo = alojamientoRepo;
        this.publicacionRepo = publicacionRepo;
        this.mapper = mapper;
    }

    @Override
    public SolicitudPublicacionResponseDTO crearSolicitud(SolicitudPublicacionRequestDTO dto) {
        Usuario usuario = usuarioRepo.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRole() != Role.OWNER) {
            throw new RuntimeException("Solo un OWNER puede solicitar publicación");
        }

        Alojamiento alojamiento = alojamientoRepo.findById(dto.alojamientoId())
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));

        SolicitudPublicacion solicitud = SolicitudPublicacion.builder()
                .usuario(usuario)
                .alojamiento(alojamiento)
                .comentario(dto.comentario())
                .estado(EstadoSolicitudPublicacion.PENDIENTE)
                .fechaSolicitud(LocalDateTime.now())
                .build();

        return mapper.toDto(solicitudRepo.save(solicitud));
    }

    @Override
    public SolicitudPublicacionResponseDTO responderSolicitud(SolicitudPublicacionRespuestaRequestDTO dto) {
        SolicitudPublicacion solicitud = solicitudRepo.findById(dto.solicitudId())
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        Usuario admin = usuarioRepo.findById(dto.adminId())
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));

        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Solo un ADMIN puede responder solicitudes");
        }

        solicitud.setAdminRevisor(admin);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitud.setComentario(dto.comentario());

        if (dto.aprobada()) {
            solicitud.setEstado(EstadoSolicitudPublicacion.APROBADA);

            // Crear publicación aprobada directamente
            Publicacion publicacion = new Publicacion();
            publicacion.setTitulo(solicitud.getAlojamiento().getNombre());
            publicacion.setDescripcion(solicitud.getAlojamiento().getDescripcion());
            publicacion.setUsuario(solicitud.getUsuario());
            publicacion.setAlojamiento(solicitud.getAlojamiento());
            publicacion.setEstado(EstadoSolicitudPublicacion.APROBADA);

            publicacionRepo.save(publicacion);

        } else {
            solicitud.setEstado(EstadoSolicitudPublicacion.RECHAZADA);
        }

        return mapper.toDto(solicitudRepo.save(solicitud));
    }

    @Override
    public List<SolicitudPublicacionResponseDTO> listarPendientes() {
        return solicitudRepo.findByEstado(EstadoSolicitudPublicacion.PENDIENTE)
                .stream().map(mapper::toDto).toList();
    }
}
