package com.clinica.arches.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO de lectura. Se usa tanto para el selector de procedimiento del módulo
 * de citas (listarActivos) como para el listado administrativo paginado y
 * el detalle. Se agregaron `categoria`, `descripcion` y `estado`, que antes
 * no se exponían.
 */
@Data
public class ProcedimientoDTO {
    private Integer idProcedimiento;
    private String nombreProcedimiento;
    private String categoria;
    private Integer duracionMinutos;
    private BigDecimal precio;
    private String descripcion;
    private String estado;
}
