package com.clinica.arches.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EvolucionDTO {
    private Integer idEvolucion;
    private Integer idPaciente;
    private Integer idPersonal;
    private String nombrePersonal;
    private String nota;
    private LocalDateTime fechaRegistro;
}
