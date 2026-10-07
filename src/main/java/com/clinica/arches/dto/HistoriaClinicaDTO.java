package com.clinica.arches.dto;

import lombok.Data;

import java.time.LocalDateTime;

/** Si el paciente aún no tiene historia, idHistoria y fechaActualizacion vienen null. */
@Data
public class HistoriaClinicaDTO {
    private Integer idHistoria;
    private Integer idPaciente;
    private String antecedentesMedicos;
    private String antecedentesOdontologicos;
    private String habitos;
    private LocalDateTime fechaActualizacion;
}
