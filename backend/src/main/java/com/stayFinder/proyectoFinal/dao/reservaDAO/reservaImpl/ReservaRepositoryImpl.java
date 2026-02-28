package com.stayFinder.proyectoFinal.dao.reservaDAO.reservaImpl;

import com.stayFinder.proyectoFinal.dao.reservaDAO.reservaCustom.ReservaRepositoryCustom;
import com.stayFinder.proyectoFinal.entity.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ReservaRepositoryImpl implements ReservaRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Reserva> buscarReservasPorRangoDeFechas(LocalDate inicio, LocalDate fin) {
        String jpql = "SELECT r FROM Reserva r WHERE r.fechaInicio >= :inicio AND r.fechaFin <= :fin";
        return entityManager.createQuery(jpql, Reserva.class)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getResultList();
    }

    @Override
    public List<Reserva> buscarReservasPorEstado(String estado) {
        String jpql = "SELECT r FROM Reserva r WHERE r.estado = :estado";
        return entityManager.createQuery(jpql, Reserva.class)
                .setParameter("estado", estado)
                .getResultList();
    }
}
