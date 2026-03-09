package com.stayFinder.proyectoFinal.services;

import com.stayFinder.proyectoFinal.dto.outputDTO.OwnerMetricsResponseDTO;
import com.stayFinder.proyectoFinal.entity.Alojamiento;
import com.stayFinder.proyectoFinal.entity.Reserva;
import com.stayFinder.proyectoFinal.entity.enums.EstadoReserva;
import com.stayFinder.proyectoFinal.repository.AlojamientoRepository;
import com.stayFinder.proyectoFinal.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnerMetricsService {

    private final AlojamientoRepository alojamientoRepo;
    private final ReservaRepository reservaRepo;

    public OwnerMetricsResponseDTO getOwnerMetrics(Long ownerId) {
        // 1. Obtener los alojamientos activos del owner
        List<Alojamiento> alojamientos = alojamientoRepo.findByOwnerIdAndEliminadoFalse(ownerId);
        long totalAlojamientos = alojamientos.size();

        // 2. Extraer reservas de todos sus alojamientos
        long totalReservas = 0;
        Map<String, Double> ingresosPorMes = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.forLanguageTag("es-ES"));

        for (Alojamiento al : alojamientos) {
            List<Reserva> reservas = reservaRepo.findByAlojamientoId(al.getId());
            totalReservas += reservas.size();

            // 3. Agrupar ingresos de las reservas confirmadas
            for (Reserva r : reservas) {
                if (r.getEstado() == EstadoReserva.CONFIRMADA) {
                    if (r.getFechaInicio() != null) {
                        String mesAnio = r.getFechaInicio().format(formatter); // Ej: "mar. 2026"

                        // Capitalizar primera letra para que se vea más bonito en el gráfico
                        mesAnio = mesAnio.substring(0, 1).toUpperCase() + mesAnio.substring(1);

                        double precioA = r.getPrecioTotal() != null ? r.getPrecioTotal() : 0.0;
                        ingresosPorMes.put(mesAnio, ingresosPorMes.getOrDefault(mesAnio, 0.0) + precioA);
                    }
                }
            }
        }

        return new OwnerMetricsResponseDTO(totalAlojamientos, totalReservas, ingresosPorMes);
    }
}
