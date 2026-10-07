package com.clinica.arches.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Fila de solo lectura del plan de tratamiento dentro del expediente (datos del módulo Tratamientos). */
@Data
public class PlanTratamientoDTO {
    private Integer idTratamiento;
    private Integer idProcedimiento;
    private String nombreProcedimiento;
    private Integer idPersonal;
    private String nombrePersonal;
    private Integer sesionesPlanificadas;
    private BigDecimal costoTotal;
    private String estadoAvance;
    private String estadoPago;
    private LocalDate fechaProgramada;
}
