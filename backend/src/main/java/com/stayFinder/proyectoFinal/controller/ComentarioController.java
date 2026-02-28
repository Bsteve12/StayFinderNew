package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.ComentarioRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.RespuestaRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ComentarioResponseDTO;
import com.stayFinder.proyectoFinal.services.comentarioService.interfaces.ComentarioServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioServiceInterface comentarioService;

    // Crear comentario por parte del usuario
    @PostMapping("/crear/{userId}")
    public ResponseEntity<ComentarioResponseDTO> crearComentario(
            @PathVariable Long userId,
            @RequestBody ComentarioRequestDTO dto
    ) {
        try {
            ComentarioResponseDTO response = comentarioService.crearComentario(userId, dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Responder a un comentario (solo anfitrión)
    @PostMapping("/responder/{ownerId}")
    public ResponseEntity<ComentarioResponseDTO> responderComentario(
            @PathVariable Long ownerId,
            @RequestBody RespuestaRequestDTO dto
    ) {
        try {
            ComentarioResponseDTO response = comentarioService.responderComentario(ownerId, dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Listar comentarios de un alojamiento
    @GetMapping("/alojamiento/{alojamientoId}")
    public ResponseEntity<List<ComentarioResponseDTO>> listarComentariosPorAlojamiento(
            @PathVariable Long alojamientoId
    ) {
        List<ComentarioResponseDTO> comentarios = comentarioService.listarComentariosPorAlojamiento(alojamientoId);
        return ResponseEntity.ok(comentarios);
    }

    // Listar comentarios de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ComentarioResponseDTO>> listarComentariosPorUsuario(
            @PathVariable Long usuarioId
    ) {
        List<ComentarioResponseDTO> comentarios = comentarioService.listarComentariosPorUsuario(usuarioId);
        return ResponseEntity.ok(comentarios);
    }

    // Eliminar comentario
    @DeleteMapping("/eliminar/{comentarioId}/{actorId}")
    public ResponseEntity<Void> eliminarComentario(
            @PathVariable Long comentarioId,
            @PathVariable Long actorId
    ) {
        try {
            comentarioService.eliminarComentario(comentarioId, actorId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
