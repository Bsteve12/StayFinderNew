package com.stayFinder.proyectoFinal.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.stayFinder.proyectoFinal.entity.enums.Role;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JWTUtil {

    @Value("${jwt.key}")
    private String jwtKey;
    @Value("${jwt.tokenExpiration}")
    private int tokenExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init(){
        secretKey = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
    }

    // CORRECCIÓN 1: Cambiamos el nombre del parámetro a 'usuarioId' y lo usamos en .setId()
    public String GenerateToken(long usuarioId, String email, Role role, String nombre){
        long expirationInMs = tokenExpiration;
        Claims claims = Jwts.claims()
                .setId(Long.toString(usuarioId)) //
                .setSubject(email)
                .setIssuer(role.toString());

        claims.put("nombre", nombre); // Guardar el nombre en el JWT

        claims.setExpiration(new Date(System.currentTimeMillis() + expirationInMs))
                .setIssuedAt(Date.from(Instant.now()));

        return Jwts.builder()
                .setClaims(claims)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String GetEmailFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    // CORRECCIÓN 2: Método para extraer el ID de Negocio (usuarioId) del token.
    public Long GetUsuarioIdFromToken(String token) {
        try {
            String idStr = extractAllClaims(token).getId();
            if (idStr != null) {
                return Long.parseLong(idStr);
            }
        } catch (JwtException | IllegalArgumentException e) {
            // Manejar error si el token no es válido o el ID no es un Long
            System.err.println("Error al extraer UsuarioId del token: " + e.getMessage());
        }
        return null;
    }

    public UUID GetSessionIdFromRefreshToken(String token){
        String sessionId = extractAllClaims(token).get("sessionId", String.class);
        return UUID.fromString(sessionId);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token) // parseClaimsJws -> para tokens firmados
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Boolean isTokenExpired(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().before(new Date());
    }

    public boolean ValidateRecoverToken(String token, String userEmail){
        try {
            final String username = extractUsername(token);
            return (username.equals(userEmail) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("JWT error: " + e.getMessage());
        }
        return false;
    }

    public boolean ValidateJwtToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("JWT error: " + e.getMessage());
        }
        return false;
    }
}