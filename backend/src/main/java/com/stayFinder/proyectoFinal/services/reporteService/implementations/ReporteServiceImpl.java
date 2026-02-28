package com.stayFinder.proyectoFinal.services.reporteService.implementations;

import com.stayFinder.proyectoFinal.dto.outputDTO.*;
import com.stayFinder.proyectoFinal.repository.ReportesRepository;
import com.stayFinder.proyectoFinal.services.reporteService.interfaces.ReporteServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteServiceInterface {

    private final ReportesRepository reportesRepository;

    @Override
    public List<ReservasPorUsuarioResponseDTO> getReservasPorUsuario() {
        return reportesRepository.obtenerReservasPorUsuario();
    }

    @Override
    public List<IngresosPorAlojamientoResponseDTO> getIngresosPorAlojamiento() {
        return reportesRepository.obtenerIngresosPorAlojamiento();
    }

    @Override
    public List<PublicacionesPendientesResponseDTO> getPublicacionesPendientes() {
        return reportesRepository.obtenerPublicacionesPendientes();
    }

    @Override
    public List<UsuariosActivosResponseDTO> getUsuariosActivos() {
        return reportesRepository.obtenerUsuariosActivos();
    }

    @Override
    public List<FavoritosPorUsuarioResponseDTO> getFavoritosPorUsuario() {
        return reportesRepository.obtenerFavoritosPorUsuario();
    }
}
