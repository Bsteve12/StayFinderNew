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
    private final EmailServiceInterface emailService; // Inyeccion del objeto

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                              UsuarioRepository usuarioRepository,
                              AlojamientoRepository alojamientoRepository,
                              ReservaMapper reservaMapper,
                              EmailServiceInterface emailService) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.alojamientoRepository = alojamientoRepository;
        this.reservaMapper = reservaMapper;
        this.emailService = emailService;
    }

    // ------------------------- MÉTODOS EXISTENTES -------------------------

    @Override
    public ReservaResponseDTO createReserva(ReservaRequestDTO dto, Long userId) throws Exception {
        // Se asume que el userId del token es el ID de Negocio (usuarioId)
        Usuario usuario = usuarioRepository.findByUsuarioId(userId)
                .orElseThrow(() -> new Exception("Usuario no existe"));

        Alojamiento alojamiento = alojamientoRepository.findById(dto.alojamientoId())
                .orElseThrow(() -> new Exception("Alojamiento no existe"));

        validarFechas(dto.fechaInicio(), dto.fechaFin());
        validarCapacidad(dto.numeroHuespedes(), alojamiento.getCapacidadMaxima());
        validarDisponibilidad(dto.fechaInicio(), dto.fechaFin(), alojamiento.getId());

        Reserva reserva = reservaMapper.toEntity(dto);
        reserva.setUsuario(usuario);
        reserva.setAlojamiento(alojamiento);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setPrecioTotal(calcularPrecioTotal(dto, alojamiento));

        Reserva saved = reservaRepository.save(reserva);

        //  Notificación de confirmación inicial (pendiente de confirmar)
        emailService.sendReservationConfirmation(
                usuario.getEmail(),
                "Reserva creada",
                "Hola " + usuario.getNombre() +
                        ", tu reserva #" + saved.getId() +
                        " fue creada y está en estado PENDIENTE."
        );

        return reservaMapper.toDto(saved);
    }

    @Override
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
                        ", tu reserva #" + reserva.getId() + " ha sido cancelada."
        );
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
                // Aquí se usa getUsuario().getId() que es la PK del usuario, lo cual está bien para este DTO temporal.
                reserva.getUsuario().getId(),
                reserva.getAlojamiento().getId(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getNumeroHuespedes(),
                reserva.getTipoReserva()
        );
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
                        ", tu reserva #" + reserva.getId() + " fue CONFIRMADA con éxito."
        );
    }

    @Override
    public List<ReservaResponseDTO> obtenerReservasUsuario(Long usuarioId) throws Exception {
        // Usar findByUsuarioId() para verificar la existencia del usuario de negocio.
        usuarioRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no existe"));

        // Se mantiene findByUsuarioId aquí, asumiendo que el método en ReservaRepository busca por la FK (usuario_id)
        return reservaRepository.findByUsuarioId(usuarioId)
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

        // Buscar por el ID de Negocio (usuarioId) del DTO
        Usuario usuario = usuarioRepository.findByUsuarioId(dto.usuarioId())
                .orElseThrow(() -> new Exception("Usuario no existe"));

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
    public List<ReservaHistorialResponseDTO> historialReservasUsuario(Long usuarioId, HistorialReservasRequestDTO filtros) throws Exception {
        usuarioRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no existe"));

        List<Reserva> reservas = reservaRepository.findByUsuarioId(usuarioId);

        return reservas.stream()
                .filter(r -> filtros.fechaInicio() == null || !r.getFechaInicio().isBefore(filtros.fechaInicio()))
                .filter(r -> filtros.fechaFin() == null || !r.getFechaFin().isAfter(filtros.fechaFin()))
                .filter(r -> filtros.estado() == null || r.getEstado() == filtros.estado())
                .map(r -> new ReservaHistorialResponseDTO(
                        r.getId(),
                        r.getAlojamiento().getId(),
                        r.getAlojamiento().getNombre(),
                        r.getUsuario().getId(),
                        r.getUsuario().getNombre(),
                        r.getFechaInicio(),
                        r.getFechaFin(),
                        r.getNumeroHuespedes(),
                        r.getPrecioTotal(),
                        r.getEstado(),
                        r.getTipoReserva()
                ))
                .toList();
    }

    @Override
    public List<ReservaHistorialResponseDTO> historialReservasAnfitrion(Long ownerId, HistorialReservasRequestDTO filtros) throws Exception {
        usuarioRepository.findByUsuarioId(ownerId)
                .orElseThrow(() -> new Exception("Usuario no existe"));

        List<Alojamiento> alojamientos = alojamientoRepository.findByOwnerId(ownerId);

        return alojamientos.stream()
                .flatMap(a -> reservaRepository.findByAlojamientoId(a.getId()).stream())
                .filter(r -> filtros.fechaInicio() == null || !r.getFechaInicio().isBefore(filtros.fechaInicio()))
                .filter(r -> filtros.fechaFin() == null || !r.getFechaFin().isAfter(filtros.fechaFin()))
                .filter(r -> filtros.estado() == null || r.getEstado() == filtros.estado())
                .map(r -> new ReservaHistorialResponseDTO(
                        r.getId(),
                        r.getAlojamiento().getId(),
                        r.getAlojamiento().getNombre(),
                        r.getUsuario().getId(),
                        r.getUsuario().getNombre(),
                        r.getFechaInicio(),
                        r.getFechaFin(),
                        r.getNumeroHuespedes(),
                        r.getPrecioTotal(),
                        r.getEstado(),
                        r.getTipoReserva()
                ))
                .toList();
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

    private void validarCapacidad(int numeroHuespedes, int capacidadMaxima) throws Exception {
        if (numeroHuespedes > capacidadMaxima) {
            throw new Exception("El número de huéspedes excede la capacidad máxima");
        }
    }

    private void validarDisponibilidad(LocalDate inicio, LocalDate fin, Long alojamientoId) throws Exception {
        List<Reserva> reservas = reservaRepository.findByAlojamientoIdAndEstado(alojamientoId, EstadoReserva.CONFIRMADA);
        for (Reserva r : reservas) {
            if (!(fin.isBefore(r.getFechaInicio()) || inicio.isAfter(r.getFechaFin()))) {
                throw new Exception("El alojamiento no está disponible en las fechas seleccionadas");
            }
        }
    }

    // =========================================================================
    // ✅ MÉTODO CORREGIDO: INCLUYE REGLA DE PERMISO PARA ADMIN
    // =========================================================================
    private Reserva obtenerReservaValida(Long reservaId, Long userId) throws Exception {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new Exception("Reserva no encontrada"));

        // 1. Obtener al ACTOR (el usuario cuyo token se está usando)
        Usuario actor = usuarioRepository.findByUsuarioId(userId)
                .orElseThrow(() -> new Exception("Actor no encontrado: ID de token inválido"));

        // 2. Comprobación de rol de ADMINISTRADOR (¡La regla de oro!)
        if (actor.getRole() == Role.ADMIN) {
            // Un administrador siempre tiene permiso para modificar o eliminar
            return reserva;
        }

        // 3. Comprobación de propietario (para roles CLIENT/OWNER)
        Long reservaOwnerId = reserva.getUsuario().getUsuarioId();

        // Verificación de integridad: el dueño de la reserva debe tener un ID de Negocio válido
        if (reservaOwnerId == null || reservaOwnerId.equals(0L)) {
            // Si el ID de Negocio del dueño de la reserva es nulo/cero, y no es Admin, denegar.
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

        double precioBase = noches * alojamiento.getPrecio();

        if (dto.tipoReserva() == TipoReserva.VIP) {
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
    public ReservaResponseDTO createReservaBasica(CreateReservaRequestDTO dto, Long usuarioId) throws Exception {
        // Buscar por el ID de Negocio (usuarioId)
        Usuario usuario = usuarioRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        Alojamiento alojamiento = alojamientoRepository.findById(dto.alojamientoId())
                .orElseThrow(() -> new Exception("Alojamiento no encontrado"));

        // Validar fecha
        LocalDate fechaReserva = LocalDate.parse(dto.fecha());
        if (fechaReserva.isBefore(LocalDate.now())) {
            throw new Exception("La fecha de reserva no puede ser en el pasado");
        }

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
                "Hola " + usuario.getNombre() + ", tu reserva #" + saved.getId() + " fue creada exitosamente."
        );

        return reservaMapper.toDto(saved);
    }

}