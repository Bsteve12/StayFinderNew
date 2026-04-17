package com.stayFinder.proyectoFinal.services.disponibilidadService;

import com.stayFinder.proyectoFinal.entity.BloqueoDisponibilidad;
import com.stayFinder.proyectoFinal.entity.Reserva;
import com.stayFinder.proyectoFinal.entity.enums.EstadoReserva;
import com.stayFinder.proyectoFinal.repository.BloqueoDisponibilidadRepository;
import com.stayFinder.proyectoFinal.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisponibilidadService {

    private final ReservaRepository reservaRepository;
    private final BloqueoDisponibilidadRepository bloqueoRepo;

    public record RangoFecha(LocalDate inicio, LocalDate fin, String tipo) {}

    public List<RangoFecha> getFechasOcupadas(Long alojamientoId) {
        List<RangoFecha> ocupadas = new ArrayList<>();

        // Obtener reservas pendientes y confirmadas
        List<Reserva> reservas = reservaRepository.findByAlojamientoId(alojamientoId).stream()
                .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA || r.getEstado() == EstadoReserva.PENDIENTE)
                .toList();

        for (Reserva r : reservas) {
            String tipo = "RESERVA_" + r.getEstado().name();
            ocupadas.add(new RangoFecha(r.getFechaInicio(), r.getFechaFin(), tipo));
        }

        // Obtener bloqueos manuales
        List<BloqueoDisponibilidad> bloqueos = bloqueoRepo.findByAlojamientoId(alojamientoId);
        for (BloqueoDisponibilidad b : bloqueos) {
            ocupadas.add(new RangoFecha(b.getFechaInicio(), b.getFechaFin(), "BLOQUEO_MANUAL"));
        }

        return ocupadas;
    }

    public boolean isDisponible(Long alojamientoId, LocalDate inicio, LocalDate fin) {
        List<RangoFecha> ocupadas = getFechasOcupadas(alojamientoId);
        for (RangoFecha r : ocupadas) {
            if (inicio.isBefore(r.fin()) && r.inicio().isBefore(fin)) {
                return false;
            }
        }
        return true;
    }
}
