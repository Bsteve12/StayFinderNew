package com.stayFinder.proyectoFinal.services.favoriteService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.FavoriteRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.FavoriteResponseDTO;
import com.stayFinder.proyectoFinal.entity.Alojamiento;
import com.stayFinder.proyectoFinal.entity.Favorite;
import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.mapper.FavoriteMapper;
import com.stayFinder.proyectoFinal.repository.AlojamientoRepository;
import com.stayFinder.proyectoFinal.repository.FavoriteRepository;
import com.stayFinder.proyectoFinal.repository.UsuarioRepository;
import com.stayFinder.proyectoFinal.services.favoriteService.interfaces.FavoriteServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteServiceInterface {

    private final FavoriteRepository favoriteRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final FavoriteMapper favoriteMapper;

    @Override
    public FavoriteResponseDTO addFavorite(FavoriteRequestDTO dto) throws Exception {
        // validar usuario
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new Exception("Usuario no existe"));

        // validar alojamiento
        Alojamiento alojamiento = alojamientoRepository.findById(dto.alojamientoId())
                .orElseThrow(() -> new Exception("Alojamiento no existe"));

        // prevenir duplicados
        if (favoriteRepository.findByUsuarioAndAlojamiento(usuario, alojamiento).isPresent()) {
            throw new Exception("Alojamiento ya está en favoritos");
        }

        Favorite favorite = Favorite.builder()
                .usuario(usuario)
                .alojamiento(alojamiento)
                .build();

        Favorite saved = favoriteRepository.save(favorite);
        return favoriteMapper.toDto(saved);
    }

    @Override
    public void removeFavorite(Long favoriteId, Long userId) throws Exception {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new Exception("Favorito no encontrado"));

        if (!favorite.getUsuario().getId().equals(userId)) {
            throw new Exception("No tienes permisos para eliminar este favorito");
        }

        favoriteRepository.delete(favorite);
    }

    @Override
    public List<FavoriteResponseDTO> listFavoritesByUsuario(Long usuarioId) throws Exception {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no existe"));

        List<Favorite> list = favoriteRepository.findByUsuario(usuario);
        return list.stream()
                .map(favoriteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFavorito(Long usuarioId, Long alojamientoId) throws Exception {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no existe"));

        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new Exception("Alojamiento no existe"));

        return favoriteRepository.findByUsuarioAndAlojamiento(usuario, alojamiento).isPresent();
    }
}
