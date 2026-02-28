package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.MensajeRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.MensajeResponseDTO;
import com.stayFinder.proyectoFinal.services.mensajeService.interfaces.MensajeServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
@RequiredArgsConstructor
public class MensajeController {

    private final MensajeServiceInterface mensajeService;

    @GetMapping("/{chatId}")
    @Operation(summary = "Listar mensajes de un chat")
    @ApiResponse(responseCode = "200", description = "Listado de mensajes por chat")
    public List<MensajeResponseDTO> listarMensajes(@PathVariable Long chatId) {
        return mensajeService.listarMensajesPorChat(chatId);
    }

    @PostMapping
    @Operation(summary = "Enviar un mensaje")
    @ApiResponse(responseCode = "201", description = "Mensaje enviado")
    public MensajeResponseDTO enviarMensaje(@RequestBody MensajeRequestDTO dto) {
        return mensajeService.enviarMensaje(dto);
    }
}
