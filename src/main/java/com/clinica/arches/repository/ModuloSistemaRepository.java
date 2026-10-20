package com.clinica.arches.repository;

import com.clinica.arches.model.ModuloSistema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuloSistemaRepository extends JpaRepository<ModuloSistema, Integer> {

    List<ModuloSistema> findAllByOrderByIdModuloAsc();
}
