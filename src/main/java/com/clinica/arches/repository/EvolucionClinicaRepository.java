package com.clinica.arches.repository;

import com.clinica.arches.model.EvolucionClinica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvolucionClinicaRepository extends JpaRepository<EvolucionClinica, Integer> {

    List<EvolucionClinica> findByIdPacienteOrderByFechaRegistroDescIdEvolucionDesc(Integer idPaciente);
}
