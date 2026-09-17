package com.clinica.arches.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProcedimientoDTO {
    private Integer idProcedimiento;
    private String nombreProcedimiento;
    private Integer duracionMinutos;
    private BigDecimal precio;
    private String estado;
}
