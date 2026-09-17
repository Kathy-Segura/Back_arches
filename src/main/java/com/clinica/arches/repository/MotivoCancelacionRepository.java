package com.clinica.arches.repository;

import com.clinica.arches.model.CatalogoMotivoCancelacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MotivoCancelacionRepository extends JpaRepository<CatalogoMotivoCancelacion, Integer> {

    List<CatalogoMotivoCancelacion> findAllByOrderByNombreMotivoAsc();
}

