package com.stayFinder.proyectoFinal.services.passwordTokenService.implementations;

import com.stayFinder.proyectoFinal.dto.inputDTO.PasswordResetTokenRequestDTO;
import com.stayFinder.proyectoFinal.dto.outputDTO.PasswordResetTokenResponseDTO;
import com.stayFinder.proyectoFinal.entity.PasswordResetToken;
import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.mapper.PasswordResetTokenMapper;
import com.stayFinder.proyectoFinal.repository.PasswordResetTokenRepository;
import com.stayFinder.proyectoFinal.repository.UsuarioRepository;
import com.stayFinder.proyectoFinal.services.passwordTokenService.interfaces.PasswordResetTokenServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenServiceInterface {

    private final PasswordResetTokenRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenMapper mapper;

    @Override
    public List<PasswordResetTokenResponseDTO> listarTokens() {
        return tokenRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PasswordResetTokenResponseDTO crearToken(PasswordResetTokenRequestDTO dto) throws Exception {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken entity = PasswordResetToken.builder()
                .usuario(usuario)
                .token(token)
                .expiresAt(LocalDateTime.now().plusMinutes(15)) // válido por 15 minutos
                .usado(false)
                .build();

        return mapper.toDto(tokenRepository.save(entity));
    }

    @Override
    public PasswordResetTokenResponseDTO validarToken(String token) throws Exception {
        PasswordResetToken entity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new Exception("Token inválido"));

        if (entity.isUsado() || entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new Exception("Token expirado o ya utilizado");
        }

        return mapper.toDto(entity);
    }

    @Override
    public void marcarComoUsado(String token) throws Exception {
        PasswordResetToken entity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new Exception("Token inválido"));

        entity.setUsado(true);
        tokenRepository.save(entity);
    }
}
