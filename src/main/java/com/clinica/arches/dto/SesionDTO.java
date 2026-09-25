package com.clinica.arches.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SesionDTO {
    private Integer idSesion;
    private Integer idTratamiento;
    private Integer idCita;
    private LocalDateTime fechaSesion;
    private String descripcionAvance;
}
