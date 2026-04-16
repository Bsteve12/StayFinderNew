package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.dao.usuarioDAO.usuarioCustom.UsuarioRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, UsuarioRepositoryCustom {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM Usuario u WHERE u.usuarioId = :cedula")
    Optional<Usuario> findByUsuarioId(@Param("cedula") Long cedula);

    @Query("SELECT u FROM Usuario u WHERE u.id = :id OR u.usuarioId = :id")
    Optional<Usuario> findAnyById(@Param("id") Long id);

    boolean existsByUsuarioId(Long usuario_id);
}
