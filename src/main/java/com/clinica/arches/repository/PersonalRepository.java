package com.clinica.arches.repository;

import com.clinica.arches.model.Personal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PersonalRepository extends JpaRepository<Personal, Integer> {

    // Sustenta el selector de "Odontólogo" en el módulo de citas
    List<Personal> findByCargoAndEstadoOrderByNombreCompletoAsc(String cargo, String estado);

    /**
     * Búsqueda para el listado del módulo de Personal: filtro opcional por
     * cargo y por nombre (contiene, sin distinguir mayúsculas), con
     * paginación real hecha en la base de datos (no se trae todo a memoria).
     * cargo y q en null significan "sin filtro".
     */
    @Query("SELECT p FROM Personal p "
            + "WHERE (:cargo IS NULL OR p.cargo = :cargo) "
            + "AND (:q IS NULL OR LOWER(p.nombreCompleto) LIKE LOWER(CONCAT('%', CAST(:q AS string), '%'))) "
            + "ORDER BY p.nombreCompleto ASC")
    Page<Personal> buscar(@Param("cargo") String cargo, @Param("q") String q, Pageable pageable);
}