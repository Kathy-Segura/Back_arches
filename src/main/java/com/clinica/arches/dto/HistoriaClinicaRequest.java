package com.clinica.arches.dto;

import lombok.Data;

@Data
public class HistoriaClinicaRequest {
    private String antecedentesMedicos;
    private String antecedentesOdontologicos;
    private String habitos;
}
