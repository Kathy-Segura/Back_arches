package com.clinica.arches.repository;

import com.clinica.arches.model.Personal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalRepository extends JpaRepository<Personal, Integer> {

    // Sustenta el selector de "Odontólogo" en el módulo de citas
    List<Personal> findByCargoAndEstadoOrderByNombreCompletoAsc(String cargo, String estado);
}
