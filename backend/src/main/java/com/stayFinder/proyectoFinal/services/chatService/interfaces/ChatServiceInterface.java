package com.stayFinder.proyectoFinal.services.chatService.interfaces;

import com.stayFinder.proyectoFinal.dto.inputDTO.ChatRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ChatResponseDTO;

import java.util.List;

public interface ChatServiceInterface {
    ChatResponseDTO crearChat(ChatRequestDTO dto);
    List<ChatResponseDTO> listarChats();
}
