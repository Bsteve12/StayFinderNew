package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.SolicitudOwnerRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.RespuestaSolicitudRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.SolicitudOwnerResponseDTO;
import com.stayFinder.proyectoFinal.services.solicitudOwnerService.interfaces.SolicitudOwnerServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes-owner")
@CrossOrigin(origins = "${frontend.url}")
@Tag(name = "Solicitudes Owner", description = "Gestión de solicitudes de anfitrión")
public class SolicitudOwnerController {

    private final SolicitudOwnerServiceInterface solicitudService;

    public SolicitudOwnerController(SolicitudOwnerServiceInterface solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crear solicitud (multipart: data + documento)")
    public ResponseEntity<SolicitudOwnerResponseDTO> crearSolicitud(
            @RequestPart("data") SolicitudOwnerRequestDTO dto,
            @RequestPart(value = "documento", required = false) MultipartFile documento) throws Exception {

        SolicitudOwnerResponseDTO resp = solicitudService.crearSolicitud(dto, documento);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/responder")
    public ResponseEntity<SolicitudOwnerResponseDTO> responderSolicitud(@RequestBody RespuestaSolicitudRequestDTO dto)
            throws Exception {
        return ResponseEntity.ok(solicitudService.responderSolicitud(dto));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudOwnerResponseDTO>> listarPendientes() throws Exception {
        return ResponseEntity.ok(solicitudService.listarSolicitudesPendientes());
    }

    @GetMapping("/descargar/{id}")
    public ResponseEntity<Resource> descargarDocumento(@PathVariable Long id) throws Exception {
        Resource file = solicitudService.descargarDocumento(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener solicitudes por usuario", description = "Devuelve el listado de solicitudes para ser owner enviadas por un usuario específico")
    public ResponseEntity<List<SolicitudOwnerResponseDTO>> obtenerSolicitudesPorUsuario(@PathVariable Long usuarioId) throws Exception {
        return ResponseEntity.ok(solicitudService.obtenerSolicitudesPorUsuario(usuarioId));
    }
}