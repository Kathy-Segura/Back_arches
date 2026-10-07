package com.clinica.arches.repository;

import com.clinica.arches.model.Diagnostico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosticoRepository extends JpaRepository<Diagnostico, Integer> {

    List<Diagnostico> findByIdPacienteOrderByFechaDiagnosticoDescIdDiagnosticoDesc(Integer idPaciente);
}
