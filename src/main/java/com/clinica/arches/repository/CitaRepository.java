package com.clinica.arches.repository;

import com.clinica.arches.model.Cita;
import com.clinica.arches.model.Personal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Integer>, JpaSpecificationExecutor<Cita> {

    /* ===== Proyecciones del dashboard ===== */

    interface MesCitasRow {
        Integer getAnio();
        Integer getMes();
        Long getTotal();
        Long getAtendidas();
    }

    interface EstadoRow {
        String getEstado();
        Long getTotal();
    }

    interface ProcedimientoRow {
        String getNombre();
        Long getTotal();
    }

    interface PersonalRow {
        Personal getPersonal();
        Long getTotal();
    }

    /* ===== Queries existentes (sin cambios) ===== */

    @Query("SELECT c FROM Cita c WHERE c.fechaHora BETWEEN :desde AND :hasta " +
            "AND (:idPersonal IS NULL OR c.personal.idPersonal = :idPersonal) " +
            "ORDER BY c.fechaHora ASC")
    List<Cita> buscarPorRango(@Param("idPersonal") Integer idPersonal,
                              @Param("desde") LocalDateTime desde,
                              @Param("hasta") LocalDateTime hasta);

    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE c.personal.idPersonal = :idPersonal " +
            "AND c.fechaHora = :fechaHora AND c.estadoCita <> 'cancelada' " +
            "AND (:idCitaExcluir IS NULL OR c.idCita <> :idCitaExcluir)")
    boolean existeChoqueHorario(@Param("idPersonal") Integer idPersonal,
                                @Param("fechaHora") LocalDateTime fechaHora,
                                @Param("idCitaExcluir") Integer idCitaExcluir);

    /* ===== Dashboard =====
     * Todos los rangos son [desde, hasta): hasta es EXCLUSIVO. */

    @Query("SELECT COUNT(c) FROM Cita c WHERE c.fechaHora >= :desde AND c.fechaHora < :hasta")
    long contarEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT COUNT(c) FROM Cita c WHERE c.fechaHora >= :desde AND c.fechaHora < :hasta " +
            "AND c.estadoCita = :estado")
    long contarEntrePorEstado(@Param("desde") LocalDateTime desde,
                              @Param("hasta") LocalDateTime hasta,
                              @Param("estado") String estado);

    @Query("""
            SELECT year(c.fechaHora) AS anio, month(c.fechaHora) AS mes,
                   COUNT(c) AS total,
                   COUNT(CASE WHEN c.estadoCita = :atendida THEN 1 END) AS atendidas
            FROM Cita c
            WHERE c.fechaHora >= :desde AND c.fechaHora < :hasta
            GROUP BY year(c.fechaHora), month(c.fechaHora)
            """)
    List<MesCitasRow> citasPorMes(@Param("desde") LocalDateTime desde,
                                  @Param("hasta") LocalDateTime hasta,
                                  @Param("atendida") String atendida);

    @Query("""
            SELECT c.estadoCita AS estado, COUNT(c) AS total
            FROM Cita c
            WHERE c.fechaHora >= :desde AND c.fechaHora < :hasta
            GROUP BY c.estadoCita
            """)
    List<EstadoRow> estadosCitas(@Param("desde") LocalDateTime desde,
                                 @Param("hasta") LocalDateTime hasta);

    @Query("""
            SELECT c.procedimiento.nombreProcedimiento AS nombre, COUNT(c) AS total
            FROM Cita c
            WHERE c.procedimiento IS NOT NULL
              AND c.fechaHora >= :desde AND c.fechaHora < :hasta
            GROUP BY c.procedimiento.nombreProcedimiento
            ORDER BY COUNT(c) DESC
            """)
    List<ProcedimientoRow> procedimientosFrecuentes(@Param("desde") LocalDateTime desde,
                                                    @Param("hasta") LocalDateTime hasta,
                                                    Pageable pageable);

    @Query("""
            SELECT c.personal AS personal, COUNT(c) AS total
            FROM Cita c
            WHERE c.fechaHora >= :desde AND c.fechaHora < :hasta
            GROUP BY c.personal
            ORDER BY COUNT(c) DESC
            """)
    List<PersonalRow> cargaOdontologos(@Param("desde") LocalDateTime desde,
                                       @Param("hasta") LocalDateTime hasta,
                                       Pageable pageable);

    /** Agenda del día con todo lo que toAgenda() necesita (evita N+1 con LAZY). */
    @Query("""
            SELECT c FROM Cita c
            JOIN FETCH c.paciente
            JOIN FETCH c.personal
            LEFT JOIN FETCH c.procedimiento
            WHERE c.fechaHora >= :desde AND c.fechaHora < :hasta
            ORDER BY c.fechaHora ASC
            """)
    List<Cita> agendaDelDia(@Param("desde") LocalDateTime desde,
                            @Param("hasta") LocalDateTime hasta);

    /** Pacientes distintos con cita en el mes que ya habían tenido una cita anterior. */
    @Query("""
            SELECT year(c.fechaHora) AS anio, month(c.fechaHora) AS mes,
                   COUNT(DISTINCT c.paciente) AS total
            FROM Cita c
            WHERE c.fechaHora >= :desde AND c.fechaHora < :hasta
              AND EXISTS (SELECT 1 FROM Cita p
                          WHERE p.paciente = c.paciente AND p.fechaHora < c.fechaHora)
            GROUP BY year(c.fechaHora), month(c.fechaHora)
            """)
    List<MesTotalRow> pacientesRecurrentesPorMes(@Param("desde") LocalDateTime desde,
                                                 @Param("hasta") LocalDateTime hasta);
}