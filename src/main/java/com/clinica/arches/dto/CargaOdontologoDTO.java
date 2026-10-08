package com.clinica.arches.dto;

import lombok.Data;
import java.math.BigDecimal;

/** Fuente: vw_carga_odontologos (citas no canceladas, histórico). */
@Data
public class CargaOdontologoDTO {
    private Integer idPersonal;
    private String odontologo;
    private Long totalCitas;
    private BigDecimal horas;
}
