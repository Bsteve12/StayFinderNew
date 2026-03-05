package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.SolicitudOwnerRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.RespuestaSolicitudRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.SolicitudOwnerResponseDTO;
import com.stayFinder.proyectoFinal.services.solicitudOwnerService.interfaces.SolicitudOwnerServiceInterface;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes-owner")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Solicitudes Owner", description = "Solicitudes para convertirse en owner y respuesta (admin)")
public class SolicitudOwnerController {

    private final SolicitudOwnerServiceInterface solicitudService;

    public SolicitudOwnerController(SolicitudOwnerServiceInterface solicitudService) {
        this.solicitudService = solicitudService;
    }

    // Multipart: parte "data" = JSON con usuarioId+comentario, parte "documento" =
    // file
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crear solicitud (multipart: data + documento)")
    public ResponseEntity<SolicitudOwnerResponseDTO> crearSolicitud(
            @RequestPart("data") SolicitudOwnerRequestDTO dto,
            @RequestPart(value = "documento", required = false) MultipartFile documento) throws Exception {

        SolicitudOwnerResponseDTO resp = solicitudService.crearSolicitud(dto, documento);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/responder")
    @Operation(summary = "Responder solicitud (admin)")
    public ResponseEntity<SolicitudOwnerResponseDTO> responderSolicitud(@RequestBody RespuestaSolicitudRequestDTO dto)
            throws Exception {
        return ResponseEntity.ok(solicitudService.responderSolicitud(dto));
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Listar solicitudes pendientes")
    public ResponseEntity<List<SolicitudOwnerResponseDTO>> listarPendientes() throws Exception {
        return ResponseEntity.ok(solicitudService.listarSolicitudesPendientes());
    }

    @GetMapping("/descargar/{id}")
    @Operation(summary = "Descargar el documento PDF de la solicitud")
    public ResponseEntity<Resource> descargarDocumento(@PathVariable Long id) throws Exception {
        Resource file = solicitudService.descargarDocumento(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}
