package com.clinica.arches.repository;

import com.clinica.arches.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {
    List<Paciente> findByEstadoExpediente(String estado);
    List<Paciente> findByNombreCompletoContainingIgnoreCase(String nombre);

    // Llama directamente al procedimiento almacenado sp_archivar_paciente
    @Modifying
    @Transactional
    @Query(value = "CALL sp_archivar_paciente(:id)", nativeQuery = true)
    void archivarPaciente(@Param("id") Integer id);
}
