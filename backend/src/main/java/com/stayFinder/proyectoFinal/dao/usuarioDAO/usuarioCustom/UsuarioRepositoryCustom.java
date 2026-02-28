package com.stayFinder.proyectoFinal.dao.usuarioDAO.usuarioCustom;

import com.stayFinder.proyectoFinal.entity.Usuario;
import java.util.List;

public interface UsuarioRepositoryCustom {
    List<Usuario> buscarUsuariosPorRol(String role);
    List<Usuario> topUsuariosConMasReservas(int limite);
}
