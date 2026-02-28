package com.stayFinder.proyectoFinal.services.mensajeService.interfaces;

import com.stayFinder.proyectoFinal.dto.inputDTO.MensajeRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.MensajeResponseDTO;

import java.util.List;

public interface MensajeServiceInterface {
    MensajeResponseDTO enviarMensaje(MensajeRequestDTO dto);
    List<MensajeResponseDTO> listarMensajesPorChat(Long chatId);
}
