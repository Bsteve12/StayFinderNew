package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.dao.usuarioDAO.usuarioCustom.UsuarioRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, UsuarioRepositoryCustom {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Usuario> findByUsuarioId(Long usuario_id);
    boolean existsByUsuarioId(Long usuario_id);

}
