package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.SolicitudPublicacionRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.SolicitudPublicacionRespuestaRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.SolicitudPublicacionResponseDTO;
import com.stayFinder.proyectoFinal.services.solicitudPublicacionService.interfaces.SolicitudPublicacionServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes-publicacion")
@CrossOrigin(origins = "${frontend.url}")
@Tag(name = "Solicitudes Publicación", description = "Solicitudes para publicar un alojamiento y su revisión por parte del admin")
public class SolicitudPublicacionController {

    private final SolicitudPublicacionServiceInterface solicitudService;

    public SolicitudPublicacionController(SolicitudPublicacionServiceInterface solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping
    @Operation(summary = "Crear solicitud de publicación", description = "OWNER crea solicitud para publicar un alojamiento")
    public ResponseEntity<SolicitudPublicacionResponseDTO> crearSolicitud(
            @RequestBody SolicitudPublicacionRequestDTO dto) {
        return ResponseEntity.ok(solicitudService.crearSolicitud(dto));
    }

    @PostMapping("/responder")
    @Operation(summary = "Responder solicitud (admin)", description = "ADMIN aprueba o rechaza la solicitud de publicación")
    public ResponseEntity<SolicitudPublicacionResponseDTO> responderSolicitud(
            @RequestBody SolicitudPublicacionRespuestaRequestDTO dto) {
        return ResponseEntity.ok(solicitudService.responderSolicitud(dto));
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Listar solicitudes pendientes", description = "Devuelve todas las solicitudes en estado PENDIENTE")
    public ResponseEntity<List<SolicitudPublicacionResponseDTO>> listarPendientes() {
        return ResponseEntity.ok(solicitudService.listarPendientes());
    }
}
