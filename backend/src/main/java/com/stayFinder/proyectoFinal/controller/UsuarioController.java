package com.stayFinder.proyectoFinal.controller;

import com.stayFinder.proyectoFinal.dto.inputDTO.CreateUserDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.LoginRequestDTO;
import com.stayFinder.proyectoFinal.dto.inputDTO.UpdateUserDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.ControllerGeneralResponseDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.LoginResponseDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.UsuarioResponseDTO;
import com.stayFinder.proyectoFinal.entity.enums.Role;
import com.stayFinder.proyectoFinal.security.UserDetailsImpl; // Importar UserDetailsImpl
import com.stayFinder.proyectoFinal.services.userService.interfaces.UserServiceInterface;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter; // Importar Parameter
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Importar AuthenticationPrincipal
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Usuarios", description = "Operaciones de usuario (registro, login, actualización, roles)")
public class UsuarioController {

    private final UserServiceInterface userService;

    @PostMapping("/login")
    @Operation(summary = "Login de usuario", description = "Autentica usuario y devuelve token JWT.")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO) throws Exception {
        return ResponseEntity.ok(userService.login(loginDTO));
    }

    @PostMapping
    @Operation(summary = "Crear usuario", description = "Crea un usuario. Si lo hace un admin puede asignar rol inicial.")
    public ResponseEntity<UsuarioResponseDTO> createUser(
            @Valid @RequestBody CreateUserDTO dto,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Long adminUsuarioId) throws Exception {
        System.out.println("Se está creando un usuario con los datos: " + dto);
        return ResponseEntity.ok(userService.createUser(dto, role, adminUsuarioId));
    }

    @PutMapping("/{usuarioId}")
    @Operation(summary = "Actualizar usuario", description = "El propio usuario o un admin puede actualizarlo. Requiere token.")
    public ResponseEntity<UsuarioResponseDTO> updateUser(
            @PathVariable Long usuarioId,
            @Valid @RequestBody UpdateUserDTO dto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user // 👈 Corregido para usar el ID del
                                                                                    // token
    ) throws Exception {
        // Se pasa el ID de Negocio del usuario autenticado (actor)
        return ResponseEntity.ok(userService.updateUser(usuarioId, dto, user.getId()));
    }

    @DeleteMapping("/{usuarioId}")
    @Operation(summary = "Eliminar usuario", description = "El propio usuario o un admin puede eliminarlo. Requiere token.")
    public ResponseEntity<ControllerGeneralResponseDTO> deleteUser(
            @PathVariable Long usuarioId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user // 👈 Corregido para usar el ID del
                                                                                    // token
    ) throws Exception {
        // Se pasa el ID de Negocio del usuario autenticado (actor)
        userService.deleteUser(usuarioId, user.getId());
        return ResponseEntity.ok(new ControllerGeneralResponseDTO(true, "Usuario eliminado correctamente"));
    }

    @PutMapping("/{usuarioId}/role")
    @Operation(summary = "Asignar rol", description = "Solo un admin puede asignar rol a otro usuario.")
    public ResponseEntity<UsuarioResponseDTO> assignRole(
            @PathVariable Long usuarioId,
            @RequestParam Role newRole,
            @RequestParam Long adminUsuarioId) throws Exception {
        // Nota: Este endpoint sigue usando adminUsuarioId, lo cual es correcto si el ID
        // del admin no viene del token.
        return ResponseEntity.ok(userService.assignRole(usuarioId, newRole, adminUsuarioId));
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Devuelve todos los usuarios registrados.")
    public ResponseEntity<List<UsuarioResponseDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Listar usuarios por rol", description = "Filtra usuarios por rol (CLIENT, OWNER, ADMIN).")
    public ResponseEntity<List<UsuarioResponseDTO>> getByRol(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsuariosPorRol(role.name()));
    }

    @PostMapping(value = "/{usuarioId}/imagen", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir imagen de perfil", description = "Sube y asocia una imagen de perfil a un usuario. Requiere token del propio usuario o admin.")
    public ResponseEntity<UsuarioResponseDTO> uploadProfileImage(
            @PathVariable Long usuarioId,
            @RequestParam("imagen") org.springframework.web.multipart.MultipartFile imagen,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user) throws Exception {
        // user.getId() es el ID de autenticación (el que cruza con usuario actor)
        return ResponseEntity.ok(userService.uploadProfileImage(usuarioId, imagen, user.getId()));
    }

    @GetMapping("/imagen/{filename:.+}")
    @Operation(summary = "Servir imagen de perfil", description = "Sirve la imagen de perfil estática subida localmente.")
    public ResponseEntity<org.springframework.core.io.Resource> serveProfileImage(@PathVariable String filename) {
        try {
            java.nio.file.Path file = java.nio.file.Paths.get("uploads/usuarios/").resolve(filename).normalize();
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}