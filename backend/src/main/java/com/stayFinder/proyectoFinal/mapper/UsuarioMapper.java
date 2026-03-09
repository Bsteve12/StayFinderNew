package com.stayFinder.proyectoFinal.mapper;

import com.stayFinder.proyectoFinal.dto.inputDTO.CreateUserDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.UsuarioResponseDTO;
import com.stayFinder.proyectoFinal.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    // 🔹 De CreateUserDTO -> Usuario
    @Mapping(source = "correo", target = "email")
    @Mapping(source = "usuarioId", target = "usuarioId") // ahora coincide con DTO y entidad
    Usuario toEntity(CreateUserDTO dto);

    // 🔹 De Usuario -> UsuarioResponseDTO
    @Mapping(source = "email", target = "correo")
    @Mapping(source = "usuarioId", target = "usuarioId")
    @Mapping(source = "imagenPerfil", target = "imagenPerfil")
    UsuarioResponseDTO toDto(Usuario usuario);
}
