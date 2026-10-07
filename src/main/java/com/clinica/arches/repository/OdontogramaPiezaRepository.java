package com.clinica.arches.repository;

import com.clinica.arches.model.OdontogramaPieza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OdontogramaPiezaRepository extends JpaRepository<OdontogramaPieza, Integer> {

    Optional<OdontogramaPieza> findByIdPacienteAndNumeroPieza(Integer idPaciente, Short numeroPieza);
}
