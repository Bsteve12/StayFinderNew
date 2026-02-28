package com.stayFinder.proyectoFinal.services.comentarioService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.ComentarioRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.RespuestaRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ComentarioResponseDTO;
import com.stayFinder.proyectoFinal.entity.Alojamiento;
import com.stayFinder.proyectoFinal.entity.Comentario;
import com.stayFinder.proyectoFinal.entity.Reserva;
import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.exceptionHandling.advices.ForbiddenException;
import com.stayFinder.proyectoFinal.exceptionHandling.advices.ResourceNotFoundException;
import com.stayFinder.proyectoFinal.repository.ComentarioRepository;
import com.stayFinder.proyectoFinal.repository.ReservaRepository;
import com.stayFinder.proyectoFinal.repository.UsuarioRepository;
import com.stayFinder.proyectoFinal.services.comentarioService.interfaces.ComentarioServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComentarioServiceImpl implements ComentarioServiceInterface {

    private final ComentarioRepository comentarioRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public ComentarioResponseDTO crearComentario(Long userId, ComentarioRequestDTO dto) {
        Reserva reserva = reservaRepository.findById(dto.reservaId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (!reserva.getUsuario().getId().equals(userId))
            throw new ForbiddenException("No puedes comentar reservas de otro usuario");

        if (reserva.getEstado() != com.stayFinder.proyectoFinal.entity.enums.EstadoReserva.CONFIRMADA)
            throw new ForbiddenException("Solo se pueden comentar reservas confirmadas y finalizadas");

        if (reserva.getFechaFin().isAfter(java.time.LocalDate.now()))
            throw new ForbiddenException("Solo se puede comentar después de finalizada la estadía");

        if (comentarioRepository.findByReserva(reserva).isPresent())
            throw new ForbiddenException("Ya existe un comentario para esta reserva");

        Comentario comentario = Comentario.builder()
                .reserva(reserva)
                .usuario(reserva.getUsuario())
                .alojamiento(reserva.getAlojamiento())
                .mensaje(dto.mensaje())
                .calificacion(dto.calificacion())
                .fechaCreacion(LocalDateTime.now())
                .build();

        comentarioRepository.save(comentario);

        return mapToDTO(comentario);
    }

    @Override
    @Transactional
    public ComentarioResponseDTO responderComentario(Long ownerId, RespuestaRequestDTO dto) {
        Comentario comentario = comentarioRepository.findById(dto.comentarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado"));

        Usuario usuario = usuarioRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Alojamiento alojamiento = comentario.getAlojamiento();
        if (!alojamiento.getOwner().getId().equals(ownerId))
            throw new ForbiddenException("Solo el anfitrión puede responder este comentario");

        comentario.setRespuestaAnfitrion(dto.mensajeRespuesta());
        comentarioRepository.save(comentario);

        return mapToDTO(comentario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComentarioResponseDTO> listarComentariosPorAlojamiento(Long alojamientoId) {
        return comentarioRepository.findByAlojamientoIdOrderByFechaCreacionDesc(alojamientoId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComentarioResponseDTO> listarComentariosPorUsuario(Long usuarioId) {
        return comentarioRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarComentario(Long comentarioId, Long actorId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado"));

        if (!comentario.getUsuario().getId().equals(actorId))
            throw new ForbiddenException("No puedes eliminar comentarios de otro usuario");

        comentarioRepository.delete(comentario);
    }

    // Método privado para mapear entidad a DTO
    private ComentarioResponseDTO mapToDTO(Comentario c) {
        return new ComentarioResponseDTO(
                c.getUsuario().getNombre(),
                c.getMensaje(),
                c.getCalificacion(),
                c.getRespuestaAnfitrion(),
                c.getAlojamiento().getOwner().getNombre(),
                c.getFechaCreacion()
        );
    }
}
