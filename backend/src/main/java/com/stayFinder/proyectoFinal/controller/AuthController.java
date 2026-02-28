/*

package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.services.userService.interfaces.UserServiceInterface;
import com.stayFinder.proyectoFinal.dto.inputDTO.LoginRequestDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Autenticación", description = "Registro y login de usuarios")
public class AuthController {

    private final UserServiceInterface usuarioService;

    public AuthController(UserServiceInterface usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario (sin lógica avanzada de DTOs por ahora).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public Usuario register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto Usuario con los datos", required = true)
            @RequestBody Usuario usuario) {
        return usuarioService.save(usuario);
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica un usuario mediante email y contraseña (ejemplo simple).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public Usuario login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "LoginRequest con email y contrasena", required = true)
            @RequestBody LoginRequestDTO loginRequest) {
        Usuario u = usuarioService.findByEmail(loginRequest.email());
        if (u != null && u.getContrasena().equals(loginRequest.contrasena())) {
            return u;
        }
        return null;
    }
    @GetMapping("/all")
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve todos los usuarios en formato DTO.")
    public List<com.stayFinder.proyectoFinal.dto.outputDTO.UsuarioResponseDTO> allUsers() {
        return usuarioService.findAll();
    }

}

 */