package com.stayFinder.proyectoFinal.services.reporteService.interfaces;

import com.stayFinder.proyectoFinal.dto.outputDTO.*;
import java.util.List;

public interface ReporteServiceInterface {
    List<ReservasPorUsuarioResponseDTO> getReservasPorUsuario();
    List<IngresosPorAlojamientoResponseDTO> getIngresosPorAlojamiento();
    List<PublicacionesPendientesResponseDTO> getPublicacionesPendientes();
    List<UsuariosActivosResponseDTO> getUsuariosActivos();
    List<FavoritosPorUsuarioResponseDTO> getFavoritosPorUsuario();
}
