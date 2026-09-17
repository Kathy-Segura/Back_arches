package com.clinica.arches.repository;

import com.clinica.arches.model.CatalogoProcedimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcedimientoRepository extends JpaRepository<CatalogoProcedimiento, Integer> {

    // Sustenta el selector de "Procedimiento" en el módulo de citas
    List<CatalogoProcedimiento> findByEstadoOrderByNombreProcedimientoAsc(String estado);
}
