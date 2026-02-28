package com.stayFinder.proyectoFinal.dao.reservaDAO.reservaCustom;

import com.stayFinder.proyectoFinal.entity.Reserva;
import java.time.LocalDate;
import java.util.List;

public interface ReservaRepositoryCustom {
    List<Reserva> buscarReservasPorRangoDeFechas(LocalDate inicio, LocalDate fin);
    List<Reserva> buscarReservasPorEstado(String estado);
}
