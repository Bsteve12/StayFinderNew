package com.stayFinder.proyectoFinal.services.reservaService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.*;
import com.stayFinder.proyectoFinal.dto.outputDTO.ReservaHistorialResponseDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ReservaResponseDTO;
import com.stayFinder.proyectoFinal.entity.Alojamiento;
import com.stayFinder.proyectoFinal.entity.Reserva;
import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.entity.enums.EstadoReserva;
import com.stayFinder.proyectoFinal.entity.enums.TipoReserva;
// IMPORTANTE: Asegúrate de que tu entidad Usuario tenga la propiedad 'Role'
import com.stayFinder.proyectoFinal.entity.enums.Role;
import com.stayFinder.proyectoFinal.mapper.ReservaMapper;
import com.stayFinder.proyectoFinal.repository.AlojamientoRepository;
import com.stayFinder.proyectoFinal.repository.ReservaRepository;
import com.stayFinder.proyectoFinal.repository.UsuarioRepository;
import com.stayFinder.proyectoFinal.services.reservaService.interfaces.ReservaServiceInterface;
import org.springframework.transaction.annotation.Transactional;
import com.stayFinder.proyectoFinal.services.emailService.interfaces.EmailServiceInterface;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaServiceInterface {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final ReservaMapper reservaMapper;
    private final EmailServiceInterface emailService;
    private final com.stayFinder.proyectoFinal.services.disponibilidadService.DisponibilidadService disponibilidadService;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
            UsuarioRepository usuarioRepository,
            AlojamientoRepository alojamientoRepository,
            ReservaMapper reservaMapper,
            EmailServiceInterface emailService,
            com.stayFinder.proyectoFinal.services.disponibilidadService.DisponibilidadService disponibilidadService) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.alojamientoRepository = alojamientoRepository;
        this.reservaMapper = reservaMapper;
        this.emailService = emailService;
        this.disponibilidadService = disponibilidadService;
    }

    // ------------------------- MÉTODOS EXISTENTES -------------------------

    @Override
    @Transactional
    public ReservaResponseDTO createReserva(ReservaRequestDTO dto, Long userId) throws Exception {
        try {
            // 🔹 MODIFICACIÓN PROCESO C: VALIDAR DISPONIBILIDAD ANTES DE NADA
            if (!disponibilidadService.isDisponible(dto.alojamientoId(), dto.fechaInicio(), dto.fechaFin())) {
                throw new Exception("Lo sentimos, estas fechas ya no están disponibles.");
            }

            Usuario usuario = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no existe"));

            Alojamiento alojamiento = alojamientoRepository.findById(dto.alojamientoId())
                    .orElseThrow(() -> new Exception("Alojamiento no existe"));

            validarFechas(dto.fechaInicio(), dto.fechaFin());
            validarCapacidad(dto.numeroHuespedes(), alojamiento.getCapacidadMaxima());
            validarDisponibilidad(dto.fechaInicio(), dto.fechaFin(), alojamiento.getId());

            // 🔹 Guardia adicional anti-spam
            List<Reserva> misPendientes = reservaRepository.findByAlojamientoIdAndEstado(alojamiento.getId(), EstadoReserva.PENDIENTE);
            boolean duplicateSpam = misPendientes.stream()
                .filter(r -> r.getUsuario() != null)
                .anyMatch(r ->
                    r.getUsuario().getId().equals(usuario.getId()) &&
                    r.getFechaInicio().equals(dto.fechaInicio()) &&
                    r.getFechaFin().equals(dto.fechaFin())
                );
            
            if (duplicateSpam) {
                throw new Exception("Ya tienes una reserva PENDIENTE para este mismo alojamiento y fechas. Proceda a pagarla en su historial.");
            }

            Reserva reserva = reservaMapper.toEntity(dto);
            reserva.setUsuario(usuario);
            reserva.setAlojamiento(alojamiento);
            reserva.setEstado(EstadoReserva.PENDIENTE);
            reserva.setPrecioTotal(calcularPrecioTotal(dto, alojamiento));

            Reserva saved = reservaRepository.save(reserva);

            // Notificación (Safe)
            try {
                emailService.sendReservationConfirmation(
                        usuario.getEmail(),
                        "Reserva creada",
                        "Hola " + usuario.getNombre() + ", tu reserva #" + saved.getId() + " fue creada y está en estado PENDIENTE.");
            } catch (Exception e) {
                System.err.println("Error enviando email: " + e.getMessage());
            }

            return reservaMapper.toDto(saved);
        } catch (Exception e) {
            System.err.println("Error Crítico en createReserva: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public void cancelarReserva(CancelarReservaRequestDTO dto, Long userId) throws Exception {
        Reserva reserva = obtenerReservaValida(dto.reservaId(), userId);

        // 🔹 Validar que falten al menos 48h para la fecha de inicio
        LocalDate hoy = LocalDate.now();
        long horasHastaCheckIn = ChronoUnit.HOURS.between(hoy.atStartOfDay(), reserva.getFechaInicio().atStartOfDay());

        if (horasHastaCheckIn < 48) {
            throw new Exception("La reserva solo puede cancelarse hasta 48 horas antes del check-in");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        // Notificación de cancelación
        emailService.sendReservationCancellation(
                reserva.getUsuario().getEmail(),
                "Reserva cancelada",
                "Hola " + reserva.getUsuario().getNombre() +
                        ", tu reserva #" + reserva.getId() + " ha sido cancelada.");
    }

    @Override
    public void deleteReserva(Long id) throws Exception {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new Exception("Reserva no encontrada"));
        reservaRepository.delete(reserva);
    }

    @Override
    public ReservaResponseDTO actualizarReserva(ActualizarReservaRequestDTO dto, Long userId) throws Exception {
        Reserva reserva = obtenerReservaValida(dto.reservaId(), userId);

        validarFechas(dto.fechaInicio(), dto.fechaFin());
        validarCapacidad(dto.numeroHuespedes(), reserva.getAlojamiento().getCapacidadMaxima());
        validarDisponibilidad(dto.fechaInicio(), dto.fechaFin(), reserva.getAlojamiento().getId());

        reserva.setFechaInicio(dto.fechaInicio());
        reserva.setFechaFin(dto.fechaFin());
        reserva.setNumeroHuespedes(dto.numeroHuespedes());
        reserva.setTipoReserva(dto.tipoReserva());

        ReservaRequestDTO tempDto = new ReservaRequestDTO(
                // Aquí se usa getUsuario().getId() que es la PK del usuario, lo cual está bien
                // para este DTO temporal.
                reserva.getUsuario().getId(),
                reserva.getAlojamiento().getId(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getNumeroHuespedes(),
                reserva.getTipoReserva());
        reserva.setPrecioTotal(calcularPrecioTotal(tempDto, reserva.getAlojamiento()));

        Reserva saved = reservaRepository.save(reserva);
        return reservaMapper.toDto(saved);
    }

    @Override
    public void confirmarReserva(Long id, Long userId) throws Exception {
        Reserva reserva = obtenerReservaValida(id, userId);
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reservaRepository.save(reserva);

        // ✅ Notificación de confirmación definitiva
        emailService.sendReservationConfirmation(
                reserva.getUsuario().getEmail(),
                "Reserva confirmada",
                "Hola " + reserva.getUsuario().getNombre() +
                        ", tu reserva #" + reserva.getId() + " fue CONFIRMADA con éxito.");
    }

    @Override
    public List<ReservaResponseDTO> obtenerReservasUsuario(Long usuarioId) throws Exception {
        // Resolver usuario de Forma Híbrida (ID o Cédula)
        Usuario usuario = usuarioRepository.findAnyById(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no existe con ID/Cédula: " + usuarioId));

        // Se mantiene findByUsuarioId aquí, asumiendo que el método en
        // ReservaRepository busca por la FK (usuario_id)
        return reservaRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(reservaMapper::toDto)
                .toList();
    }

    @Override
    public List<ReservaResponseDTO> obtenerReservasPorAlojamiento(Long alojamientoId) throws Exception {
        alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new Exception("Alojamiento no encontrado"));
        return reservaRepository.findByAlojamientoId(alojamientoId)
                .stream()
                .map(reservaMapper::toDto)
                .toList();
    }

    @Override
    public Optional<ReservaResponseDTO> findById(Long id) {
        return reservaRepository.findById(id).map(reservaMapper::toDto);
    }

    @Override
    public ReservaResponseDTO save(ReservaRequestDTO dto) throws Exception {
        Alojamiento alojamiento = alojamientoRepository.findById(dto.alojamientoId())
                .orElseThrow(() -> new Exception("Alojamiento no existe"));

        // 🔹 MODIFICACIÓN PROCESO C: VALIDAR DISPONIBILIDAD
        if (!disponibilidadService.isDisponible(dto.alojamientoId(), dto.fechaInicio(), dto.fechaFin())) {
            throw new Exception("Lo sentimos, estas fechas ya no están disponibles. Alguien más podría estar reservando o el anfitrión ha bloqueado estos días.");
        }

        Usuario usuario = usuarioRepository.findAnyById(dto.usuarioId())
                .orElseThrow(() -> new Exception("Usuario no existe con ID/Cédula: " + dto.usuarioId()));

        Reserva reserva = reservaMapper.toEntity(dto);
        reserva.setUsuario(usuario);
        reserva.setAlojamiento(alojamiento);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setPrecioTotal(calcularPrecioTotal(dto, alojamiento));

        Reserva saved = reservaRepository.save(reserva);
        return reservaMapper.toDto(saved);
    }

    @Override
    public void deleteById(Long id, Long userId) throws Exception {
        Reserva reserva = obtenerReservaValida(id, userId);
        reservaRepository.delete(reserva);
    }

    // ------------------------- NUEVOS MÉTODOS: HISTORIAL -------------------------

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<ReservaHistorialResponseDTO> historialReservasUsuario(Long usuarioId,
            HistorialReservasRequestDTO filtros) throws Exception {
        // Resolver usuario de Forma Híbrida para Historial
        Usuario usuario = usuarioRepository.findAnyById(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no encontrado con ID/Cédula: " + usuarioId));

        List<Reserva> reservas = reservaRepository.findByUsuarioId(usuario.getId());

        return reservas.stream()
                .filter(r -> filtros.fechaInicio() == null || !r.getFechaInicio().isBefore(filtros.fechaInicio()))
                .filter(r -> filtros.fechaFin() == null || !r.getFechaFin().isAfter(filtros.fechaFin()))
                .filter(r -> filtros.estado() == null || r.getEstado() == filtros.estado())
                .map(r -> new ReservaHistorialResponseDTO(
                        r.getId(),
                        r.getAlojamiento().getId(),
                        r.getAlojamiento().getNombre(),
                        (r.getAlojamiento().getImagenes() != null && !r.getAlojamiento().getImagenes().isEmpty() ? r.getAlojamiento().getImagenes().get(0).getUrl() : null),
                        r.getUsuario().getId(),
                        r.getUsuario().getNombre(),
                        r.getFechaInicio(),
                        r.getFechaFin(),
                        r.getNumeroHuespedes(),
                        (r.getPrecioTotal() != null ? r.getPrecioTotal() : 0.0),
                        (r.getCreatedAt() != null ? r.getCreatedAt().toLocalDate() : LocalDate.now()),
                        r.getEstado(),
                        r.getTipoReserva()))
                .toList();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<ReservaHistorialResponseDTO> historialReservasAnfitrion(Long ownerId,
            HistorialReservasRequestDTO filtros) throws Exception {
        Usuario owner = usuarioRepository.findAnyById(ownerId)
                .orElseThrow(() -> new Exception("Usuario no existe"));

        System.out.println("[HistorialAnfitrion] Owner encontrado: " + owner.getNombre() + " (ID PK: " + owner.getId() + ")");

        List<Alojamiento> alojamientos = alojamientoRepository.findByOwnerId(owner.getId());
        System.out.println("[HistorialAnfitrion] Alojamientos encontrados: " + alojamientos.size());

        List<ReservaHistorialResponseDTO> list = alojamientos.stream()
                .flatMap(a -> reservaRepository.findByAlojamientoId(a.getId()).stream())
                .filter(r -> filtros.fechaInicio() == null || !r.getFechaInicio().isBefore(filtros.fechaInicio()))
                .filter(r -> filtros.fechaFin() == null || !r.getFechaFin().isAfter(filtros.fechaFin()))
                .filter(r -> filtros.estado() == null || r.getEstado() == filtros.estado())
                .map(r -> new ReservaHistorialResponseDTO(
                        r.getId(),
                        r.getAlojamiento().getId(),
                        r.getAlojamiento().getNombre(),
                        (r.getAlojamiento().getImagenes() != null && !r.getAlojamiento().getImagenes().isEmpty() ? r.getAlojamiento().getImagenes().get(0).getUrl() : null),
                        r.getUsuario().getId(),
                        r.getUsuario().getNombre(),
                        r.getFechaInicio(),
                        r.getFechaFin(),
                        r.getNumeroHuespedes(),
                        (r.getPrecioTotal() != null ? r.getPrecioTotal() : 0.0),
                        (r.getCreatedAt() != null ? r.getCreatedAt().toLocalDate() : LocalDate.now()),
                        r.getEstado(),
                        r.getTipoReserva()))
                .toList();

        System.out.println("[HistorialAnfitrion] Reservas filtradas totales: " + list.size());
        return list;
    }

    // ------------------------- MÉTODOS AUXILIARES -------------------------

    private void validarFechas(LocalDate inicio, LocalDate fin) throws Exception {
        if (inicio.isAfter(fin)) {
            throw new Exception("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        if (inicio.isBefore(LocalDate.now())) {
            throw new Exception("La fecha de inicio no puede ser en el pasado");
        }
    }

    private void validarCapacidad(int numeroHuespedes, Integer capacidadMaxima) throws Exception {
        if (capacidadMaxima != null && numeroHuespedes > capacidadMaxima) {
            throw new Exception("El número de huéspedes excede la capacidad máxima");
        }
    }

    private void validarDisponibilidad(LocalDate inicio, LocalDate fin, Long alojamientoId) throws Exception {
        // Mejorado Proceso C: Usar el servicio centralizado que incluye Bloqueos Manuales
        if (!disponibilidadService.isDisponible(alojamientoId, inicio, fin)) {
            throw new Exception("Lo sentimos, estas fechas ya no están disponibles. Alguien más podría estar reservando o el anfitrión ha bloqueado estos días.");
        }
    }

    // =========================================================================
    // ✅ MÉTODO CORREGIDO: INCLUYE REGLA DE PERMISO PARA ADMIN
    // =========================================================================
    private Reserva obtenerReservaValida(Long reservaId, Long userId) throws Exception {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new Exception("Reserva no encontrada"));

        // 1. Obtener al ACTOR de forma robusta (PK o Cédula)
        Usuario actor = usuarioRepository.findAnyById(userId)
                .orElseThrow(() -> new Exception("Actor no encontrado con ID/Cédula: " + userId));

        // 2. Comprobación de rol de ADMINISTRADOR (¡La regla de oro!)
        if (actor.getRole() == Role.ADMIN) {
            // Un administrador siempre tiene permiso para modificar o eliminar
            return reserva;
        }

        // 3. Comprobación de propietario (para roles CLIENT/OWNER)
        Long reservaOwnerId = reserva.getUsuario().getUsuarioId();

        // Verificación de integridad: el dueño de la reserva debe tener un ID de
        // Negocio válido
        if (reservaOwnerId == null || reservaOwnerId.equals(0L)) {
            // Si el ID de Negocio del dueño de la reserva es nulo/cero, y no es Admin,
            // denegar.
            throw new Exception("Error de integridad: El dueño de la reserva no tiene un ID de Negocio válido.");
        }

        // Si el actor no es el dueño de la reserva, denegar permiso
        if (!reservaOwnerId.equals(userId)) {
            throw new Exception("No tiene permiso para modificar esta reserva");
        }

        return reserva;
    }

    private double calcularPrecioTotal(ReservaRequestDTO dto, Alojamiento alojamiento) {
        long noches = ChronoUnit.DAYS.between(dto.fechaInicio(), dto.fechaFin());
        if (noches <= 0) {
            noches = 1;
        }

        double precioBaseAlojamiento = (alojamiento.getPrecio() != null) ? alojamiento.getPrecio() : 0.0;
        double precioBase = noches * precioBaseAlojamiento;

        if (dto.tipoReserva() != null && dto.tipoReserva() == TipoReserva.VIP) {
            if (noches > 7) {
                precioBase *= 0.5;
            }
            precioBase *= 0.9;
        } else {
            precioBase *= 0.95;
        }

        return precioBase;
    }

    @Override
    @Transactional
    public ReservaResponseDTO createReservaBasica(CreateReservaRequestDTO dto, Long usuarioId) throws Exception {
        // Buscar usando la PK base de datos
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        Alojamiento alojamiento = alojamientoRepository.findById(dto.alojamientoId())
                .orElseThrow(() -> new Exception("Alojamiento no encontrado"));

        // Validar fecha
        LocalDate fechaReserva = LocalDate.parse(dto.fecha());
        if (fechaReserva.isBefore(LocalDate.now())) {
            throw new Exception("La fecha de reserva no puede ser en el pasado");
        }

        // 🔹 MODIFICACIÓN PROCESO C: VALIDAR DISPONIBILIDAD (Reservas + Bloqueos)
        validarDisponibilidad(fechaReserva, fechaReserva.plusDays(1), alojamiento.getId());

        // Crear la reserva con estado PENDIENTE
        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .alojamiento(alojamiento)
                .fechaInicio(fechaReserva)
                .fechaFin(fechaReserva.plusDays(1)) // Por defecto 1 noche
                .numeroHuespedes(1)
                .precioTotal(alojamiento.getPrecio())
                .estado(EstadoReserva.PENDIENTE)
                .tipoReserva(TipoReserva.SENCILLA)
                .build();

        Reserva saved = reservaRepository.save(reserva);

        // Notificación simple
        emailService.sendReservationConfirmation(
                usuario.getEmail(),
                "Reserva creada",
                "Hola " + usuario.getNombre() + ", tu reserva #" + saved.getId() + " fue creada exitosamente.");

        return reservaMapper.toDto(saved);
    }

}