package com.stayFinder.proyectoFinal.services.favoriteService.interfaces;

import com.stayFinder.proyectoFinal.dto.inputDTO.FavoriteRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.FavoriteResponseDTO;

import java.util.List;

public interface FavoriteServiceInterface {

    // Agrega alojamiento a favoritos (devuelve el DTO creado)
    FavoriteResponseDTO addFavorite(FavoriteRequestDTO dto) throws Exception;

    // Elimina favorito por id (valida que el user sea el dueño del favorito)
    void removeFavorite(Long favoriteId, Long userId) throws Exception;

    // Lista favoritos de un usuario
    List<FavoriteResponseDTO> listFavoritesByUsuario(Long usuarioId) throws Exception;

    // Opcional: verifica si un alojamiento ya está en favoritos del usuario
    boolean isFavorito(Long usuarioId, Long alojamientoId) throws Exception;
}
