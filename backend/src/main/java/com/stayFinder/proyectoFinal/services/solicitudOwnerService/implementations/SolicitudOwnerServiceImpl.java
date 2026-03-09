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
import com.stayFinder.proyectoFinal.services.emailService.interfaces.EmailServiceInterface;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final EmailServiceInterface emailService;

    // Ruta dinámica: Se creará en la raíz donde se ejecute el proyecto
    private static final String UPLOAD_DIR = "uploads/solicitudes";

    @Override
    public SolicitudOwnerResponseDTO crearSolicitud(SolicitudOwnerRequestDTO dto, MultipartFile documento)
            throws Exception {

        // Forzamos la limpieza de caché buscando el usuario directamente
        Usuario usuario = usuarioRepo.findByUsuarioId(dto.usuarioId())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Debug log para verificar qué rol está llegando (puedes verlo en consola)
        System.out.println(
                "Verificando solicitud para usuario: " + usuario.getEmail() + " con Rol: " + usuario.getRole());

        // Corregimos la validación para asegurar que compare correctamente el Enum
        if (!usuario.getRole().equals(Role.CLIENT)) {
            throw new Exception(
                    "Solo usuarios con rol CLIENT pueden enviar solicitudes. Tu rol actual es: " + usuario.getRole());
        }

        // No permitir más de una solicitud pendiente
        boolean yaPendiente = solicitudRepo.findByEstado(EstadoSolicitud.PENDIENTE)
                .stream()
                .anyMatch(s -> s.getUsuario().getId().equals(usuario.getId()));

        if (yaPendiente)
            throw new Exception("El usuario ya tiene una solicitud pendiente");

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

        String adminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario admin = usuarioRepo.findByEmail(adminEmail)
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

            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendHostApplicationDecision(solicitud.getUsuario().getEmail(),
                            "Resolución de tu solicitud para Anfitrión",
                            "Hola " + solicitud.getUsuario().getNombre() + ",\n\n" +
                                    "Lamentamos informarte que tu solicitud fue rechazada automáticamente debido a que el límite de revisión de 3 días hábiles expiró.");
                } catch (Exception e) {
                    System.err.println("Error enviando email: " + e.getMessage());
                }
            });

            return mapper.toDto(solicitud);
        }

        if (dto.aprobada()) {
            solicitud.setEstado(EstadoSolicitud.APROBADA);

            // Cambiar rol del usuario a OWNER
            Usuario usuario = solicitud.getUsuario();
            usuario.setRole(Role.OWNER);
            usuarioRepo.save(usuario);

            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendHostApplicationDecision(usuario.getEmail(),
                            "¡Felicidades! Ahora eres Anfitrión en StayFinder",
                            "Hola " + usuario.getNombre() + ",\n\n" +
                                    "Tu solicitud ha sido aprobada. Ahora puedes administrar y publicar tus propiedades desde el panel de control.\n\n"
                                    +
                                    "¡Bienvenido al equipo de anfitriones!");
                } catch (Exception e) {
                    System.err.println("Error enviando email: " + e.getMessage());
                }
            });
        } else {
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);

            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendHostApplicationDecision(solicitud.getUsuario().getEmail(),
                            "Resolución de tu solicitud para Anfitrión",
                            "Hola " + solicitud.getUsuario().getNombre() + ",\n\n" +
                                    "Lamentamos informarte que tu solicitud no ha sido aprobada en esta ocasión.\n\n" +
                                    "Comentarios del administrador: " + dto.comentario());
                } catch (Exception e) {
                    System.err.println("Error enviando email: " + e.getMessage());
                }
            });
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

    private String guardarDocumento(MultipartFile file, Long usuarioId) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new Exception("Debe adjuntar un documento PDF");
        }
        try {
            // Obtener la ruta de la raíz del proyecto de forma dinámica
            Path root = Paths.get("").toAbsolutePath();
            Path uploadPath = root.resolve(UPLOAD_DIR).normalize();

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = "solicitud_" + usuarioId + "_" + System.currentTimeMillis() + ".pdf";
            Path destino = uploadPath.resolve(fileName);

            // Copiar el archivo
            Files.copy(file.getInputStream(), destino);

            // Retornamos la ruta absoluta para que no se pierda al mover carpetas
            return destino.toString();
        } catch (IOException e) {
            throw new Exception("Error crítico al guardar documento: " + e.getMessage());
        }
    }

    @Override
    public Resource descargarDocumento(Long id) throws Exception {
        SolicitudOwner solicitud = solicitudRepo.findById(id)
                .orElseThrow(() -> new Exception("Solicitud no encontrada"));

        String ruta = solicitud.getDocumentoRuta();
        if (ruta == null || ruta.isEmpty()) {
            throw new Exception("Esta solicitud no tiene un documento asociado");
        }

        try {
            Path file = Paths.get(ruta).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new Exception("El archivo no existe en la ruta: " + ruta);
            }
        } catch (Exception e) {
            throw new Exception("Error al cargar el archivo: " + e.getMessage());
        }
    }

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