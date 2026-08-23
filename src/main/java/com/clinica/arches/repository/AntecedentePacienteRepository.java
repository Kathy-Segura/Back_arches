package com.clinica.arches.repository;

import com.clinica.arches.model.AntecedentePaciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AntecedentePacienteRepository extends JpaRepository<AntecedentePaciente, Integer> {
    List<AntecedentePaciente> findByIdPaciente(Integer idPaciente);
}
