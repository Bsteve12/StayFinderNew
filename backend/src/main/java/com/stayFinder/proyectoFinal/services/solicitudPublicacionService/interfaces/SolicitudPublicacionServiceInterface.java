package com.stayFinder.proyectoFinal.services.solicitudPublicacionService.interfaces;

import com.stayFinder.proyectoFinal.dto.inputDTO.SolicitudPublicacionRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.SolicitudPublicacionRespuestaRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.SolicitudPublicacionResponseDTO;

import java.util.List;

public interface SolicitudPublicacionServiceInterface {
    SolicitudPublicacionResponseDTO crearSolicitud(SolicitudPublicacionRequestDTO dto);
    SolicitudPublicacionResponseDTO responderSolicitud(SolicitudPublicacionRespuestaRequestDTO dto);
    List<SolicitudPublicacionResponseDTO> listarPendientes();
}
