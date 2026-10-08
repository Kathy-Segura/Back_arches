package com.clinica.arches.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Fila del reporte "Procedimientos realizados" (una fila por tratamiento). */
@Data
public class ReporteProcedimientoDTO {
    private Integer idTratamiento;
    private String paciente;
    private String procedimiento;
    private String categoria;
    private LocalDate fechaProgramada;
    private String odontologo;
    private BigDecimal costoTotal;
    private String estadoAvance;
    private String estadoPago;
}
