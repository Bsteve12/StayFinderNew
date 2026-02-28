package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.PublicacionRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.PublicacionResponseDTO;
import com.stayFinder.proyectoFinal.services.publicacionService.interfaces.PublicacionServiceInterface;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/publicaciones")
@Tag(name = "Publicaciones", description = "Gestión de publicaciones (pendientes, aprobar, rechazar)")
public class PublicacionController {

    private final PublicacionServiceInterface publicacionService;

    public PublicacionController(PublicacionServiceInterface publicacionService) {
        this.publicacionService = publicacionService;
    }

    @PostMapping
    @Operation(summary = "Crear publicación", description = "Crea una publicación asociada a un usuario.")
    public PublicacionResponseDTO crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la publicación", required = true)
            @RequestBody PublicacionRequestDTO dto) {
        return publicacionService.crearPublicacion(dto);
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Listar publicaciones pendientes", description = "Devuelve las publicaciones en estado PENDIENTE.")
    public List<PublicacionResponseDTO> listarPendientes() {
        return publicacionService.listarPendientes();
    }

    @PutMapping("/{id}/aprobar")
    @Operation(summary = "Aprobar publicación", description = "Cambia el estado de la publicación a APROBADA.")
    public PublicacionResponseDTO aprobar(
            @Parameter(description = "ID de la publicación", required = true, example = "1")
            @PathVariable Long id) {
        return publicacionService.aprobarPublicacion(id);
    }

    @PutMapping("/{id}/rechazar")
    @Operation(summary = "Rechazar publicación", description = "Cambia el estado de la publicación a RECHAZADA.")
    public PublicacionResponseDTO rechazar(
            @Parameter(description = "ID de la publicación", required = true, example = "1")
            @PathVariable Long id) {
        return publicacionService.rechazarPublicacion(id);
    }
}
