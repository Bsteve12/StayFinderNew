package com.stayFinder.proyectoFinal.dao.alojamientoDAO.alojamientoImpl;

import com.stayFinder.proyectoFinal.dao.alojamientoDAO.alojamientoCustom.AlojamientoRepositoryCustom;
import com.stayFinder.proyectoFinal.entity.Publicacion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AlojamientoRepositoryImpl implements AlojamientoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Publicacion> buscarPorCiudad(String ciudad) {
        String jpql = "SELECT a FROM Publicacion a WHERE a.ciudad = :ciudad";
        return entityManager.createQuery(jpql, Publicacion.class)
                .setParameter("ciudad", ciudad)
                .getResultList();
    }

    @Override
    public List<Publicacion> buscarPorRangoPrecio(double min, double max) {
        String jpql = "SELECT a FROM Publicacion a WHERE a.precioPorNoche BETWEEN :min AND :max";
        return entityManager.createQuery(jpql, Publicacion.class)
                .setParameter("min", min)
                .setParameter("max", max)
                .getResultList();
    }
}
