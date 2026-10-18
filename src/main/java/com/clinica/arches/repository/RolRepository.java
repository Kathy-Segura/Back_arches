package com.clinica.arches.repository;

import com.clinica.arches.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    List<Rol> findAllByOrderByIdRolAsc();
    List<Rol> findByActivoTrueOrderByNombreRol();
    List<Rol> findByActivoTrueOrderByIdRolAsc();

    boolean existsByNombreRolIgnoreCase(String nombreRol);

    boolean existsByNombreRolIgnoreCaseAndIdRolNot(String nombreRol, Integer idRol);

}
