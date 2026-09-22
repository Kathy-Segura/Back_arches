package com.clinica.arches.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/** DTO de entrada para crear/editar un procedimiento desde el módulo administrativo. */
@Data
public class ProcedimientoRequestDTO {

    @NotBlank
    private String nombreProcedimiento;

    @NotBlank
    private String categoria;

    @NotNull
    @Positive
    private Integer duracionMinutos;

    @NotNull
    @Positive
    private BigDecimal precio;

    private String descripcion;
}
