package com.stayFinder.proyectoFinal.services.solicitudOwnerService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.SolicitudOwnerRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.RespuestaSolicitudRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.SolicitudOwnerResponseDTO;
import com.stayFinder.proyectoFinal.entity.SolicitudOwner;
import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.entity.enums.EstadoSolicitud;
import com.stayFinder.proyectoFinal.entity.enums.Role;
import com.stayFinder.proyectoFinal.mapper.SolicitudOwnerMapper;
import com.stayFinder.proyectoFinal.repository.SolicitudOwnerRepository;
import com.stayFinder.proyectoFinal.repository.UsuarioRepository;
import com.stayFinder.proyectoFinal.services.solicitudOwnerService.interfaces.SolicitudOwnerServiceInterface;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitudOwnerServiceImpl implements SolicitudOwnerServiceInterface {

    private final SolicitudOwnerRepository solicitudRepo;
    private final UsuarioRepository usuarioRepo;
    private final SolicitudOwnerMapper mapper;

    private static final String UPLOAD_DIR = "uploads/solicitudes/";

    @Override
    public SolicitudOwnerResponseDTO crearSolicitud(SolicitudOwnerRequestDTO dto, MultipartFile documento) throws Exception {
        Usuario usuario = usuarioRepo.findById(dto.usuarioId())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        if (usuario.getRole() != Role.CLIENT) {
            throw new Exception("Solo usuarios con rol CLIENT pueden enviar solicitudes");
        }

        // No permitir más de una solicitud pendiente
        boolean yaPendiente = solicitudRepo.findByEstado(EstadoSolicitud.PENDIENTE)
                .stream()
                .anyMatch(s -> s.getUsuario().getId().equals(usuario.getId()));

        if (yaPendiente) throw new Exception("El usuario ya tiene una solicitud pendiente");

        String ruta = null;
        if (documento != null && !documento.isEmpty()) {
            ruta = guardarDocumento(documento, usuario.getId());
        }

        SolicitudOwner solicitud = SolicitudOwner.builder()
                .usuario(usuario)
                .comentario(dto.comentario())
                .estado(EstadoSolicitud.PENDIENTE)
                .documentoRuta(ruta)
                .fechaSolicitud(LocalDateTime.now())
                .build();

        SolicitudOwner saved = solicitudRepo.save(solicitud);
        return mapper.toDto(saved);
    }

    @Override
    public SolicitudOwnerResponseDTO responderSolicitud(RespuestaSolicitudRequestDTO dto) throws Exception {
        SolicitudOwner solicitud = solicitudRepo.findById(dto.solicitudId())
                .orElseThrow(() -> new Exception("Solicitud no encontrada"));

        Usuario admin = usuarioRepo.findById(dto.adminId())
                .orElseThrow(() -> new Exception("Admin no encontrado"));

        if (admin.getRole() != Role.ADMIN) {
            throw new Exception("Solo administradores pueden responder solicitudes");
        }

        // Verificar plazo de 3 días hábiles
        LocalDateTime limite = sumarDiasHabiles(solicitud.getFechaSolicitud(), 3);

        solicitud.setAdminRevisor(admin);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitud.setComentario(dto.comentario());

        if (LocalDateTime.now().isAfter(limite)) {
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);
            solicitudRepo.save(solicitud);
            return mapper.toDto(solicitud);
        }

        if (dto.aprobada()) {
            solicitud.setEstado(EstadoSolicitud.APROBADA);

            // Cambiar rol del usuario a OWNER
            Usuario usuario = solicitud.getUsuario();
            usuario.setRole(Role.OWNER);
            usuarioRepo.save(usuario);
        } else {
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        }

        solicitudRepo.save(solicitud);
        return mapper.toDto(solicitud);
    }

    @Override
    public List<SolicitudOwnerResponseDTO> listarSolicitudesPendientes() throws Exception {
        return solicitudRepo.findByEstado(EstadoSolicitud.PENDIENTE)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    // Guarda el PDF en disco y devuelve la ruta
    private String guardarDocumento(MultipartFile file, Long usuarioId) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new Exception("Debe adjuntar un documento PDF");
        }
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String fileName = "solicitud_" + usuarioId + "_" + System.currentTimeMillis() + ".pdf";
            File destino = new File(dir, fileName);
            file.transferTo(destino);
            return destino.getAbsolutePath();
        } catch (IOException e) {
            throw new Exception("Error al guardar el documento: " + e.getMessage(), e);
        }
    }

    // suma 'dias' hábiles ignorando sábados y domingos
    private LocalDateTime sumarDiasHabiles(LocalDateTime fecha, int dias) {
        int agregados = 0;
        LocalDateTime actual = fecha;
        while (agregados < dias) {
            actual = actual.plusDays(1);
            if (!(actual.getDayOfWeek() == DayOfWeek.SATURDAY || actual.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                agregados++;
            }
        }
        return actual;
    }
}
