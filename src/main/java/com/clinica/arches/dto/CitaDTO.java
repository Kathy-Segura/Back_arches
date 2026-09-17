package com.clinica.arches.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO de salida para el listado y el calendario de citas.
 * El "código" (C-0001) que se ve en la tabla del front se genera del lado
 * del cliente a partir de idCita (misma convención usada con P-0001 en
 * pacientes), así que no viaja en este DTO.
 */
@Data
public class CitaDTO {
    private Integer idCita;

    private Integer idPaciente;
    private String nombrePaciente;

    private Integer idPersonal;
    private String nombrePersonal;

    private Integer idProcedimiento;
    private String nombreProcedimiento;

    private LocalDateTime fechaHora;
    private Integer duracionMinutos;
    private String estadoCita;

    private Integer idMotivo;
    private String nombreMotivo;

    private String notas;
}

