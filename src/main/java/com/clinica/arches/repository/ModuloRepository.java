package com.clinica.arches.repository;

import com.clinica.arches.model.Modulo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuloRepository extends JpaRepository<Modulo, Integer> {

    List<Modulo> findAllByOrderByIdModuloAsc();
}
