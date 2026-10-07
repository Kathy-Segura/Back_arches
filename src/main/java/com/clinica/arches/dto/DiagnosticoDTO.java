package com.clinica.arches.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DiagnosticoDTO {
    private Integer idDiagnostico;
    private Integer idPaciente;
    private Integer idPersonal;
    private String nombrePersonal;
    private String diagnostico;
    private String descripcion;
    private LocalDate fechaDiagnostico;
}
