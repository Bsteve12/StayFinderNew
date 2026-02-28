package com.stayFinder.proyectoFinal.dao.alojamientoDAO.alojamientoCustom;

import com.stayFinder.proyectoFinal.entity.Publicacion;
import java.util.List;

public interface AlojamientoRepositoryCustom {
    List<Publicacion> buscarPorCiudad(String ciudad);
    List<Publicacion> buscarPorRangoPrecio(double min, double max);
}
