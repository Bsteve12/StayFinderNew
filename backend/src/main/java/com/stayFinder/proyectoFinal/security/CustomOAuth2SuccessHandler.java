package com.stayFinder.proyectoFinal.security;

import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.entity.enums.Role;
import com.stayFinder.proyectoFinal.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Usuario usuario;
        Optional<Usuario> userOptional = usuarioRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            usuario = userOptional.get();
        } else {
            // Usuario nuevo
            Long newUsuarioId = Math.abs(UUID.randomUUID().getMostSignificantBits());
            while(usuarioRepository.existsByUsuarioId(newUsuarioId)) {
                newUsuarioId = Math.abs(UUID.randomUUID().getMostSignificantBits());
            }

            usuario = Usuario.builder()
                    .email(email)
                    .nombre(name)
                    .usuarioId(newUsuarioId)
                    .role(Role.CLIENT)
                    .contrasena(passwordEncoder.encode(UUID.randomUUID().toString())) // Contraseña inusable aleatoria
                    .build();

            usuario = usuarioRepository.save(usuario);
        }

        String jwt = jwtUtil.GenerateToken(usuario.getUsuarioId(), usuario.getEmail(), usuario.getRole(), usuario.getNombre());

        // Redirigir al frontend con el token en la URL
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:4200/oauth2/redirect")
                .queryParam("token", jwt)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
