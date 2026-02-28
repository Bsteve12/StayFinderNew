package com.stayFinder.proyectoFinal.mapper;

import com.stayFinder.proyectoFinal.dto.inputDTO.FavoriteRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.FavoriteResponseDTO;
import com.stayFinder.proyectoFinal.entity.Alojamiento;
import com.stayFinder.proyectoFinal.entity.Favorite;
import com.stayFinder.proyectoFinal.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

    // Favorite -> FavoriteResponseDTO (mapear ids)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "alojamiento.id", target = "alojamientoId")
    FavoriteResponseDTO toDto(Favorite favorite);

    // FavoriteRequestDTO -> Favorite (crea relaciones mínimas con id)
    // MapStruct usará los métodos helper fromUsuarioId / fromAlojamientoId automáticamente si los definimos.
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "usuarioId", target = "usuario")
    @Mapping(source = "alojamientoId", target = "alojamiento")
    Favorite toEntity(FavoriteRequestDTO dto);

    // helpers: convierten id -> entidad con solo el id seteado (útil para evitar búsquedas en DB por MapStruct)
    default Usuario mapUsuario(Long usuarioId) {
        if (usuarioId == null) return null;
        Usuario u = new Usuario();
        u.setId(usuarioId);
        return u;
    }

    default Alojamiento mapAlojamiento(Long alojamientoId) {
        if (alojamientoId == null) return null;
        Alojamiento a = new Alojamiento();
        a.setId(alojamientoId);
        return a;
    }
}
