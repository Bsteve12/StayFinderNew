package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.dao.alojamientoDAO.alojamientoCustom.AlojamientoRepositoryCustom;
import com.stayFinder.proyectoFinal.entity.Alojamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long>, AlojamientoRepositoryCustom {

    // Lista solo alojamientos activos (no eliminados) por propietario
    List<Alojamiento> findByOwnerIdAndEliminadoFalse(Long ownerId);

    // Lista todos los alojamientos activos
    List<Alojamiento> findByEliminadoFalse();

    // Método para obtener alojamientos por ownerId
    List<Alojamiento> findByOwnerId(Long ownerId);


}
