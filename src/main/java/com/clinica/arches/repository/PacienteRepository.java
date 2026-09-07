package com.clinica.arches.repository;

import com.clinica.arches.model.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    List<Paciente> findByEstadoExpediente(String estado);
    List<Paciente> findByNombreCompletoContainingIgnoreCase(String nombre);

    /**
     * Sustenta la barra de búsqueda (nombre O cédula) + el filtro de estado (activo/inactivo)
     * + la tabla paginada del listado de pacientes.
     * Si "search" o "estado" vienen null, ese filtro se ignora (no restringe la consulta).
     */
    @Query("SELECT p FROM Paciente p WHERE " +
            "(:search IS NULL OR LOWER(p.nombreCompleto) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "     OR LOWER(p.cedula) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:estado IS NULL OR p.estadoExpediente = :estado)")
    Page<Paciente> buscarConFiltros(@Param("search") String search,
                                    @Param("estado") String estado,
                                    Pageable pageable);

    /**
     * Misma búsqueda pero sin paginar, usada para generar los archivos de exportación
     * (Excel/PDF), que deben incluir TODOS los registros que cumplen el filtro, no solo
     * la página visible en la tabla.
     */
    @Query("SELECT p FROM Paciente p WHERE " +
            "(:search IS NULL OR LOWER(p.nombreCompleto) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "     OR LOWER(p.cedula) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:estado IS NULL OR p.estadoExpediente = :estado) " +
            "ORDER BY p.nombreCompleto ASC")
    List<Paciente> buscarConFiltrosSinPaginar(@Param("search") String search,
                                              @Param("estado") String estado);

    // Llama directamente al procedimiento almacenado sp_archivar_paciente
    @Modifying
    @Transactional
    @Query(value = "CALL clinica.sp_archivar_paciente(CAST(:id AS integer))", nativeQuery = true)
    void archivarPaciente(@Param("id") Integer id);
}
