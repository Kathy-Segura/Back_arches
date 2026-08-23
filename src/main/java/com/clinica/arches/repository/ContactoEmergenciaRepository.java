package com.clinica.arches.repository;

import com.clinica.arches.model.ContactoEmergencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactoEmergenciaRepository extends JpaRepository<ContactoEmergencia, Integer> {
    List<ContactoEmergencia> findByIdPaciente(Integer idPaciente);
}
