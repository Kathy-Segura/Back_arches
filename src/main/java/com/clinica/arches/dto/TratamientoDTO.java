package com.clinica.arches.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para el listado y el detalle de un tratamiento.
 *
 * No resuelve nombre de paciente ni de procedimiento: el back no tiene
 * visibilidad de esas tablas todavía, así que solo expone los ids
 * (idPaciente, idProcedimiento). El nombre de odontólogo sí se resuelve
 * (personalNombre), porque el módulo de Personal ya existe.
 *
 * "sesiones" queda null en el listado (para no traer historiales completos
 * de todos los tratamientos de golpe) y se llena solo al pedir el detalle
 * de uno con GET /api/tratamientos/{id}.
 */
@Data
public class TratamientoDTO {
    private Integer idTratamiento;
    private Integer idPaciente;
    private Integer idProcedimiento;
    private Integer idPersonal;
    private String personalNombre;
    private LocalDate fechaProgramada;
    private BigDecimal costoTotal;
    private Integer sesionesPlanificadas;
    private String estadoAvance;
    private String estadoPago;
    private String notas;
    private LocalDateTime fechaCreacion;
    private List<SesionDTO> sesiones;
}
