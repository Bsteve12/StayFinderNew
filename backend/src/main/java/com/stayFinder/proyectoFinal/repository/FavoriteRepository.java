package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.entity.Alojamiento;
import com.stayFinder.proyectoFinal.entity.Favorite;
import com.stayFinder.proyectoFinal.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUsuario(Usuario usuario);

    // útil para evitar duplicados
    Optional<Favorite> findByUsuarioAndAlojamiento(Usuario usuario, Alojamiento alojamiento);
}
