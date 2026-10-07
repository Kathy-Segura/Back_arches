package com.clinica.arches.repository;

import com.clinica.arches.model.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Integer> {

    Optional<HistoriaClinica> findFirstByIdPacienteOrderByIdHistoriaDesc(Integer idPaciente);
}
