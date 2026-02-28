package com.stayFinder.proyectoFinal.services.solicitudOwnerService.interfaces;

import com.stayFinder.proyectoFinal.dto.inputDTO.SolicitudOwnerRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.RespuestaSolicitudRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.SolicitudOwnerResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SolicitudOwnerServiceInterface {
    SolicitudOwnerResponseDTO crearSolicitud(SolicitudOwnerRequestDTO dto, MultipartFile documento) throws Exception;
    SolicitudOwnerResponseDTO responderSolicitud(RespuestaSolicitudRequestDTO dto) throws Exception;
    List<SolicitudOwnerResponseDTO> listarSolicitudesPendientes() throws Exception;
}
