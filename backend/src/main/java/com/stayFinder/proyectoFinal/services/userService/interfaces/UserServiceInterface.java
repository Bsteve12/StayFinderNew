package com.stayFinder.proyectoFinal.services.userService.interfaces;

import java.util.List;

import com.stayFinder.proyectoFinal.dto.inputDTO.CreateUserDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.LoginRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.LoginResponseDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.UpdateUserDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.UsuarioResponseDTO;
import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.entity.enums.Role;

public interface UserServiceInterface {

    // Manejo de usuarios
    UsuarioResponseDTO createUser(CreateUserDTO createUserDTO, Role roleSolicitado, Long adminUsuarioId) throws Exception;
    UsuarioResponseDTO updateUser(Long usuarioId, UpdateUserDTO updateUserDTO, Long actorUsuarioId) throws Exception;
    void deleteUser(Long usuarioId, Long actorUsuarioId) throws Exception;
    UsuarioResponseDTO assignRole(Long usuarioId, Role newRole, Long adminUsuarioId) throws Exception;

    // Autenticación
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO) throws Exception;

    // CRUD básico de Usuario
    Usuario findByEmail(String email);
    Usuario save(Usuario usuario);
    List<UsuarioResponseDTO> findAll();
    List<UsuarioResponseDTO> getUsuariosPorRol(String rol);
}
