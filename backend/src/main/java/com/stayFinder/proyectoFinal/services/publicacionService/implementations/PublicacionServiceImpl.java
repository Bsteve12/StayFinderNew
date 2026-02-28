package com.stayFinder.proyectoFinal.services.publicacionService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.PublicacionRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.PublicacionResponseDTO;
import com.stayFinder.proyectoFinal.entity.Publicacion;
import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.entity.enums.EstadoSolicitudPublicacion;
import com.stayFinder.proyectoFinal.entity.enums.Role;
import com.stayFinder.proyectoFinal.mapper.PublicacionMapper;
import com.stayFinder.proyectoFinal.repository.PublicacionRepository;
import com.stayFinder.proyectoFinal.repository.UsuarioRepository;
import com.stayFinder.proyectoFinal.services.publicacionService.interfaces.PublicacionServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicacionServiceImpl implements PublicacionServiceInterface {

    private final PublicacionRepository publicacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PublicacionMapper mapper;

    public PublicacionServiceImpl(PublicacionRepository publicacionRepository,
                                  UsuarioRepository usuarioRepository,
                                  PublicacionMapper mapper) {
        this.publicacionRepository = publicacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
    }

    @Override
    public PublicacionResponseDTO crearPublicacion(PublicacionRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validaciones de roles
        if (usuario.getRole() == Role.CLIENT) {
            throw new RuntimeException("Un cliente no puede crear publicaciones. Solo OWNER o ADMIN.");
        }

        Publicacion publicacion = mapper.toEntity(dto);
        publicacion.setUsuario(usuario);

        // Estado según rol
        if (usuario.getRole() == Role.ADMIN) {
            publicacion.setEstado(EstadoSolicitudPublicacion.APROBADA);
        } else if (usuario.getRole() == Role.OWNER) {
            publicacion.setEstado(EstadoSolicitudPublicacion.PENDIENTE);
        }

        return mapper.toDto(publicacionRepository.save(publicacion));
    }

    @Override
    public List<PublicacionResponseDTO> listarPendientes() {
        return publicacionRepository.findByEstado(EstadoSolicitudPublicacion.PENDIENTE)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PublicacionResponseDTO aprobarPublicacion(Long id) {
        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        // Validación de estado
        if (publicacion.getEstado() != EstadoSolicitudPublicacion.PENDIENTE) {
            throw new RuntimeException("Solo se pueden aprobar publicaciones en estado PENDIENTE.");
        }

        publicacion.setEstado(EstadoSolicitudPublicacion.APROBADA);
        return mapper.toDto(publicacionRepository.save(publicacion));
    }

    @Override
    public PublicacionResponseDTO rechazarPublicacion(Long id) {
        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

        if (publicacion.getEstado() != EstadoSolicitudPublicacion.PENDIENTE) {
            throw new RuntimeException("Solo se pueden rechazar publicaciones en estado PENDIENTE.");
        }

        publicacion.setEstado(EstadoSolicitudPublicacion.RECHAZADA);
        return mapper.toDto(publicacionRepository.save(publicacion));
    }
}
