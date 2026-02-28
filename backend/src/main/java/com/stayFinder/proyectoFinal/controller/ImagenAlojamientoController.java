package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.ImagenAlojamientoRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ImagenAlojamientoResponseDTO;
import com.stayFinder.proyectoFinal.services.imagenService.interfaces.ImagenAlojamientoServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/imagenes-alojamiento")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Imágenes Alojamiento", description = "Gestión de imágenes asociadas a un alojamiento")
public class ImagenAlojamientoController {

    private final ImagenAlojamientoServiceInterface imagenService;

    public ImagenAlojamientoController(ImagenAlojamientoServiceInterface imagenService) {
        this.imagenService = imagenService;
    }

    @PostMapping
    @Operation(summary = "Subir imagen", description = "Agrega una nueva imagen a un alojamiento.")
    public ResponseEntity<ImagenAlojamientoResponseDTO> subir(@RequestBody ImagenAlojamientoRequestDTO dto) throws Exception {
        return ResponseEntity.ok(imagenService.subirImagen(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener imagen por ID", description = "Devuelve la información de una imagen específica.")
    public ResponseEntity<ImagenAlojamientoResponseDTO> obtener(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(imagenService.obtenerImagen(id));
    }

    @GetMapping("/alojamiento/{alojamientoId}")
    @Operation(summary = "Listar imágenes de un alojamiento", description = "Devuelve todas las imágenes asociadas a un alojamiento.")
    public ResponseEntity<List<ImagenAlojamientoResponseDTO>> listarPorAlojamiento(@PathVariable Long alojamientoId) throws Exception {
        return ResponseEntity.ok(imagenService.listarPorAlojamiento(alojamientoId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar imagen", description = "Elimina una imagen de un alojamiento.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) throws Exception {
        imagenService.eliminarImagen(id);
        return ResponseEntity.noContent().build();
    }
}
