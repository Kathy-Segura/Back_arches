package com.clinica.arches.repository;

import com.clinica.arches.model.Cita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

// Se agrega JpaSpecificationExecutor: permite construir la query de forma
// dinámica (solo se agregan los WHERE de los filtros que realmente vienen).
// Esto evita mandar parámetros null en la SQL final, que es lo que rompía
// con PostgreSQL tanto en su variante con CAST (bytea) como sin CAST
// (could not determine data type of parameter).
public interface CitaRepository extends JpaRepository<Cita, Integer>, JpaSpecificationExecutor<Cita> {

    /**
     * Sustenta la pestaña "Calendario": todas las citas de un rango de fechas, sin paginar.
     * Aquí :desde y :hasta NO son opcionales (el calendario siempre manda un
     * rango), así que no hay ambigüedad de tipo y esta query se deja igual.
     */
    @Query("SELECT c FROM Cita c WHERE c.fechaHora BETWEEN :desde AND :hasta " +
            "AND (:idPersonal IS NULL OR c.personal.idPersonal = :idPersonal) " +
            "ORDER BY c.fechaHora ASC")
    List<Cita> buscarPorRango(@Param("idPersonal") Integer idPersonal,
                              @Param("desde") LocalDateTime desde,
                              @Param("hasta") LocalDateTime hasta);

    // --------------------------------------------------------------------------------------------------//
    /**
     * Chequeo simple de doble-reserva: ¿el mismo odontólogo ya tiene una cita
     * (no cancelada) exactamente a esa fecha/hora? Es una validación básica
     * por igualdad exacta, no un choque por solapamiento de duración; se puede
     * refinar más adelante si se requiere.
     * idPersonal y fechaHora tampoco son opcionales aquí, solo idCitaExcluir
     * lo es, y al ser Integer comparado con "<>" (no con >=/<= de timestamp)
     * no dispara el mismo bug de inferencia de tipo.
     */
    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE c.personal.idPersonal = :idPersonal " +
            "AND c.fechaHora = :fechaHora AND c.estadoCita <> 'cancelada' " +
            "AND (:idCitaExcluir IS NULL OR c.idCita <> :idCitaExcluir)")
    boolean existeChoqueHorario(@Param("idPersonal") Integer idPersonal,
                                @Param("fechaHora") LocalDateTime fechaHora,
                                @Param("idCitaExcluir") Integer idCitaExcluir);
}
