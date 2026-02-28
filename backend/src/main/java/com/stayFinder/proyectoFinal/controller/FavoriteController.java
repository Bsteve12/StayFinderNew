package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.FavoriteRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.FavoriteResponseDTO;
import com.stayFinder.proyectoFinal.services.favoriteService.interfaces.FavoriteServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Favoritos", description = "Gestión de alojamientos favoritos por usuario")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteServiceInterface favoriteService;

    @PostMapping
    @Operation(summary = "Agregar a favoritos", description = "Permite a un usuario marcar un alojamiento como favorito.")
    public ResponseEntity<FavoriteResponseDTO> agregar(@RequestBody FavoriteRequestDTO dto) throws Exception {
        return ResponseEntity.ok(favoriteService.addFavorite(dto));
    }

    @DeleteMapping("/{id}/usuario/{usuarioId}")
    @Operation(summary = "Eliminar favorito", description = "Quita un alojamiento de la lista de favoritos.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @PathVariable Long usuarioId) throws Exception {
        favoriteService.removeFavorite(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar favoritos de un usuario", description = "Devuelve los alojamientos favoritos de un usuario.")
    public ResponseEntity<List<FavoriteResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) throws Exception {
        return ResponseEntity.ok(favoriteService.listFavoritesByUsuario(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/alojamiento/{alojamientoId}")
    @Operation(summary = "Verificar si es favorito", description = "Permite verificar si un alojamiento está en favoritos de un usuario.")
    public ResponseEntity<Boolean> isFavorito(@PathVariable Long usuarioId, @PathVariable Long alojamientoId) throws Exception {
        return ResponseEntity.ok(favoriteService.isFavorito(usuarioId, alojamientoId));
    }
}
