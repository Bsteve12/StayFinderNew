package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.ServicioRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ServicioResponseDTO;
import com.stayFinder.proyectoFinal.services.servicioService.interfaces.ServicioServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@Tag(name = "Servicios", description = "Gestión de servicios adicionales (aseo, chef, VIP)")
public class ServicioController {

    private final ServicioServiceInterface servicioService;

    @PostMapping
    @Operation(summary = "Crear servicio", description = "Crea un nuevo servicio con nombre y precio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Servicio creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ServicioResponseDTO> crear(@RequestBody ServicioRequestDTO dto) {
        return ResponseEntity.ok(servicioService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar servicio", description = "Modifica un servicio existente")
    public ResponseEntity<ServicioResponseDTO> editar(@PathVariable Long id, @RequestBody ServicioRequestDTO dto) {
        return ResponseEntity.ok(servicioService.editar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar servicio", description = "Elimina un servicio por ID")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Listar todos los servicios", description = "Devuelve todos los servicios disponibles")
    public ResponseEntity<List<ServicioResponseDTO>> listar() {
        return ResponseEntity.ok(servicioService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener servicio por ID", description = "Devuelve un servicio específico")
    public ResponseEntity<ServicioResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(servicioService.obtenerPorId(id));
    }
}
