package com.clinica.arches.repository;

import com.clinica.arches.model.CatalogoProcedimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProcedimientoRepository extends JpaRepository<CatalogoProcedimiento, Integer> {

    // Sustenta el selector de "Procedimiento" en el módulo de citas. No se tocó.
    List<CatalogoProcedimiento> findByEstadoOrderByNombreProcedimientoAsc(String estado);

    /**
     * Búsqueda para el listado del módulo de Procedimientos: filtro opcional
     * por categoría y por nombre (contiene, sin distinguir mayúsculas), con
     * paginación real hecha en la base de datos (no se trae todo a memoria).
     * categoria y q en null significan "sin filtro".
     */
    @Query("SELECT p FROM CatalogoProcedimiento p "
            + "WHERE (:categoria IS NULL OR p.categoria = :categoria) "
            + "AND (:q IS NULL OR LOWER(p.nombreProcedimiento) LIKE LOWER(CONCAT('%', CAST(:q AS string), '%'))) "
            + "ORDER BY p.nombreProcedimiento ASC")
    Page<CatalogoProcedimiento> buscar(@Param("categoria") String categoria, @Param("q") String q, Pageable pageable);
}
