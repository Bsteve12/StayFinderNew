package com.stayFinder.proyectoFinal.services.passwordResetService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.PasswordResetTokenRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.PasswordResetTokenResponseDTO;
import com.stayFinder.proyectoFinal.entity.PasswordResetToken;
import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.repository.PasswordResetTokenRepository;
import com.stayFinder.proyectoFinal.repository.UsuarioRepository;
import com.stayFinder.proyectoFinal.services.emailService.interfaces.EmailServiceInterface;
import com.stayFinder.proyectoFinal.services.passwordResetService.interfaces.PasswordResetServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetServiceInterface {

    private final UsuarioRepository usuarioRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final EmailServiceInterface emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PasswordResetTokenResponseDTO generarToken(PasswordResetTokenRequestDTO dto) throws Exception {
        Usuario usuario = usuarioRepo.findById(dto.getUsuarioId())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Generar un token único
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .usuario(usuario)
                .token(token)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .usado(false)
                .build();

        tokenRepo.save(resetToken);

        // --- CAMBIO AQUÍ: Generar el enlace real para el Frontend ---
        String urlFrontend = "http://localhost:4200/reset-password?token=" + token;

        String body = "Hola " + usuario.getNombre() + ",\n\n"
                + "Hemos recibido una solicitud para restablecer tu contraseña en StayFinder.\n"
                + "Para completar el proceso, haz clic en el siguiente enlace:\n\n"
                + urlFrontend + "\n\n"
                + "Este enlace es válido por 15 minutos.\n"
                + "Si no solicitaste este cambio, puedes ignorar este mensaje de forma segura.";

        // Usamos tu servicio de email para enviar el link real
        emailService.sendReservationConfirmation(usuario.getEmail(), "Recuperación de contraseña - StayFinder", body);

        return PasswordResetTokenResponseDTO.builder()
                .id(resetToken.getId())
                .usuarioId(usuario.getId())
                .token(resetToken.getToken())
                .expiresAt(resetToken.getExpiresAt())
                .usado(resetToken.isUsado())
                .build();
    }

    @Override
    public void resetPassword(String token, String nuevaPassword) throws Exception {
        // Buscar el token en la base de datos
        PasswordResetToken resetToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new Exception("El enlace de recuperación es inválido o ya no existe."));

        // Validar si ya fue usado o si expiró
        if (resetToken.isUsado()) {
            throw new Exception("Este enlace ya ha sido utilizado.");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new Exception("El enlace ha expirado. Por favor, solicita uno nuevo.");
        }

        // Obtener el usuario asociado y actualizar la contraseña codificada
        Usuario usuario = resetToken.getUsuario();
        usuario.setContrasena(passwordEncoder.encode(nuevaPassword));
        usuarioRepo.save(usuario);

        // Marcar el token como usado para que no se pueda volver a usar
        resetToken.setUsado(true);
        tokenRepo.save(resetToken);
    }
}