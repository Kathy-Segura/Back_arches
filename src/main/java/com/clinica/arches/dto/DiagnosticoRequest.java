package com.clinica.arches.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DiagnosticoRequest {
    private Integer idPersonal;
    private String diagnostico;
    private String descripcion;
    /** Opcional: si no viene, se usa la fecha de hoy. */
    private LocalDate fechaDiagnostico;
}
