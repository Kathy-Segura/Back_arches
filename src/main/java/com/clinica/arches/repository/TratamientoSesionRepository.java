package com.clinica.arches.repository;

import com.clinica.arches.model.TratamientoSesion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TratamientoSesionRepository extends JpaRepository<TratamientoSesion, Integer> {
    List<TratamientoSesion> findByIdTratamientoOrderByFechaSesionAsc(Integer idTratamiento);
}
