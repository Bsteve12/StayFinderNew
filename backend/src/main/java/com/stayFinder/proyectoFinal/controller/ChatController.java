package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.ChatRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ChatResponseDTO;
import com.stayFinder.proyectoFinal.services.chatService.interfaces.ChatServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatServiceInterface chatService;

    @GetMapping
    @Operation(summary = "Listar todos los chats")
    @ApiResponse(responseCode = "200", description = "Listado de chats")
    public List<ChatResponseDTO> listarChats() {
        return chatService.listarChats();
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo chat")
    @ApiResponse(responseCode = "201", description = "Chat creado")
    public ChatResponseDTO crearChat(@RequestBody ChatRequestDTO dto) {
        return chatService.crearChat(dto);
    }
}
