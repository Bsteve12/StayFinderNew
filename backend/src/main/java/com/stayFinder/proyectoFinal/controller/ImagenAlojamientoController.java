package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.ImagenAlojamientoRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ImagenAlojamientoResponseDTO;
import com.stayFinder.proyectoFinal.services.imagenService.interfaces.ImagenAlojamientoServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/imagenes-alojamiento")
@CrossOrigin(origins = "${frontend.url}")
@Tag(name = "Imágenes Alojamiento", description = "Gestión de imágenes asociadas a un alojamiento")
public class ImagenAlojamientoController {

    private final ImagenAlojamientoServiceInterface imagenService;

    public ImagenAlojamientoController(ImagenAlojamientoServiceInterface imagenService) {
        this.imagenService = imagenService;
    }

    @PostMapping
    @Operation(summary = "Subir imagen", description = "Agrega una nueva imagen a un alojamiento mediante URL.")
    public ResponseEntity<ImagenAlojamientoResponseDTO> subir(@RequestBody ImagenAlojamientoRequestDTO dto)
            throws Exception {
        return ResponseEntity.ok(imagenService.subirImagen(dto));
    }

    @PostMapping(value = "/multiples", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir múltiples imágenes físicas y URls", description = "Sube varios archivos de imagen y URLs a un alojamiento.")
    public ResponseEntity<List<ImagenAlojamientoResponseDTO>> subirMultiples(
            @RequestParam("alojamientoId") Long alojamientoId,
            @RequestParam(value = "imagenes", required = false) List<MultipartFile> imagenes,
            @RequestParam(value = "nuevasUrls", required = false) List<String> nuevasUrls) throws Exception {
        return ResponseEntity.ok(imagenService.subirImagenes(alojamientoId, imagenes, nuevasUrls));
    }

    @GetMapping("/file/{filename:.+}")
    @Operation(summary = "Servir imagen", description = "Sirve la imagen estática subida localmente.")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = Paths.get("uploads/alojamientos/").resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                // Return generic image type if not detected. Can be improved by checking the
                // file extension.
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener imagen por ID", description = "Devuelve la información de una imagen específica.")
    public ResponseEntity<ImagenAlojamientoResponseDTO> obtener(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(imagenService.obtenerImagen(id));
    }

    @GetMapping("/alojamiento/{alojamientoId}")
    @Operation(summary = "Listar imágenes de un alojamiento", description = "Devuelve todas las imágenes asociadas a un alojamiento.")
    public ResponseEntity<List<ImagenAlojamientoResponseDTO>> listarPorAlojamiento(@PathVariable Long alojamientoId)
            throws Exception {
        return ResponseEntity.ok(imagenService.listarPorAlojamiento(alojamientoId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar imagen", description = "Elimina una imagen de un alojamiento.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) throws Exception {
        imagenService.eliminarImagen(id);
        return ResponseEntity.noContent().build();
    }
}
