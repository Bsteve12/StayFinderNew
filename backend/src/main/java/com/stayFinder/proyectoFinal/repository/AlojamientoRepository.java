package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.dao.alojamientoDAO.alojamientoCustom.AlojamientoRepositoryCustom;
import com.stayFinder.proyectoFinal.entity.Alojamiento;
import com.stayFinder.proyectoFinal.entity.enums.EstadoAlojamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long>, AlojamientoRepositoryCustom {

    // Lista solo alojamientos activos (no eliminados) por propietario
    List<Alojamiento> findByOwnerIdAndEliminadoFalse(Long ownerId);

    // Lista todos los alojamientos activos de verdad (usando el estado)
    List<Alojamiento> findByEstadoAndEliminadoFalse(EstadoAlojamiento estado);

    // Método para obtener alojamientos por ownerId
    List<Alojamiento> findByOwnerId(Long ownerId);

    // KPI: Cuenta alojamientos por estado y que no estén eliminados
    long countByEstadoAndEliminadoFalse(EstadoAlojamiento estado);

    // KPI: Total de alojamientos creados a partir de una fecha específica
    long countByCreatedAtAfter(LocalDateTime date);

    // KPI: Obtiene la cantidad de alojamientos agrupados por estado
    @Query("SELECT a.estado AS estado, COUNT(a) AS total FROM Alojamiento a WHERE a.eliminado = false GROUP BY a.estado")
    List<Map<String, Object>> findCountByEstado();

    // KPI: Métricas de Eliminación
    long countByEliminadoTrue();

    // KPI: Métricas de Edición
    @Query("SELECT COUNT(a) FROM Alojamiento a WHERE a.updatedAt > a.createdAt")
    long countEditedAlojamientos();

}
