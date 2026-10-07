package com.clinica.arches.repository;

import com.clinica.arches.model.Paciente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Lecturas sobre las vistas clinica.vw_expediente_resumen y clinica.vw_odontograma_actual.
 * Se usan consultas nativas con proyecciones (no entidades) porque las vistas no tienen
 * clave primaria. Los alias van entre comillas para que coincidan con los getters.
 */
public interface ExpedienteVistaRepository extends Repository<Paciente, Integer> {

    interface ResumenRow {
        Integer getIdPaciente();
        String getNombreCompleto();
        Integer getEdad();
        String getEstadoExpediente();
        LocalDateTime getHistoriaClinicaActualizada();
        Long getTotalDiagnosticos();
        Long getTotalNotasEvolucion();
        Long getTotalCitasAtendidas();
    }

    interface PiezaRow {
        Integer getNumeroPieza();
        String getCodigoHallazgo();
        String getNombreHallazgo();
        String getColorRepresentativo();
        LocalDateTime getFechaActualizacion();
    }

    @Query(value = """
            SELECT id_paciente                  AS "idPaciente",
                   nombre_completo              AS "nombreCompleto",
                   edad                         AS "edad",
                   estado_expediente            AS "estadoExpediente",
                   historia_clinica_actualizada AS "historiaClinicaActualizada",
                   total_diagnosticos           AS "totalDiagnosticos",
                   total_notas_evolucion        AS "totalNotasEvolucion",
                   total_citas_atendidas        AS "totalCitasAtendidas"
            FROM clinica.vw_expediente_resumen
            WHERE id_paciente = :idPaciente
            """, nativeQuery = true)
    Optional<ResumenRow> resumen(@Param("idPaciente") Integer idPaciente);

    @Query(value = """
            SELECT numero_pieza         AS "numeroPieza",
                   codigo_hallazgo      AS "codigoHallazgo",
                   nombre_hallazgo      AS "nombreHallazgo",
                   color_representativo AS "colorRepresentativo",
                   fecha_actualizacion  AS "fechaActualizacion"
            FROM clinica.vw_odontograma_actual
            WHERE id_paciente = :idPaciente
            ORDER BY numero_pieza
            """, nativeQuery = true)
    List<PiezaRow> odontogramaActual(@Param("idPaciente") Integer idPaciente);
}
