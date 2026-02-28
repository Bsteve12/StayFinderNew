package com.stayFinder.proyectoFinal.services.comentarioService.interfaces;

import com.stayFinder.proyectoFinal.dto.inputDTO.ComentarioRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.RespuestaRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ComentarioResponseDTO;

import java.util.List;

public interface ComentarioServiceInterface {

    ComentarioResponseDTO crearComentario(Long userId, ComentarioRequestDTO dto) throws Exception;


    ComentarioResponseDTO responderComentario(Long ownerId, RespuestaRequestDTO dto);

    List<ComentarioResponseDTO> listarComentariosPorAlojamiento(Long alojamientoId);

    List<ComentarioResponseDTO> listarComentariosPorUsuario(Long usuarioId);

    void eliminarComentario(Long comentarioId, Long actorId);
}
