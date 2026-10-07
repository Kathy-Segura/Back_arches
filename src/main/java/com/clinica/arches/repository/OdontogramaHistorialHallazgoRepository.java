package com.clinica.arches.repository;

import com.clinica.arches.model.OdontogramaHistorialHallazgo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OdontogramaHistorialHallazgoRepository extends JpaRepository<OdontogramaHistorialHallazgo, Integer> {

    List<OdontogramaHistorialHallazgo> findByIdPiezaOrderByFechaRegistroDescIdRegistroDesc(Integer idPieza);
}
