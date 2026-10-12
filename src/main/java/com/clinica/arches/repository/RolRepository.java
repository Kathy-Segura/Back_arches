package com.clinica.arches.repository;

import com.clinica.arches.model.Rol;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    List<Rol> findAllByOrderByIdRolAsc();

    List<Rol> findByActivoTrueOrderByIdRolAsc();

    boolean existsByNombreRolIgnoreCase(String nombreRol);

    boolean existsByNombreRolIgnoreCaseAndIdRolNot(String nombreRol, Integer idRol);
}
