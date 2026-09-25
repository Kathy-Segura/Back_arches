package com.clinica.arches.repository;

import com.clinica.arches.model.Tratamiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TratamientoRepository extends JpaRepository<Tratamiento, Integer> {

    /**
     * Filtros opcionales: idPaciente, idPersonal, estadoAvance, estadoPago.
     * No filtra por nombre de paciente porque el back no tiene el esquema
     * de clinica.pacientes; ese filtro se resuelve en el frontend eligiendo
     * el paciente de una lista y filtrando por su id.
     */
    @Query("SELECT t FROM Tratamiento t "
            + "WHERE (:idPaciente IS NULL OR t.idPaciente = :idPaciente) "
            + "AND (:idPersonal IS NULL OR t.idPersonal = :idPersonal) "
            + "AND (:estadoAvance IS NULL OR t.estadoAvance = :estadoAvance) "
            + "AND (:estadoPago IS NULL OR t.estadoPago = :estadoPago) "
            + "ORDER BY t.fechaCreacion DESC")
    Page<Tratamiento> buscar(
            @Param("idPaciente") Integer idPaciente,
            @Param("idPersonal") Integer idPersonal,
            @Param("estadoAvance") String estadoAvance,
            @Param("estadoPago") String estadoPago,
            Pageable pageable
    );
}
