package com.clinica.arches.dto;

import lombok.Data;

/** DTO liviano usado por el selector de odontólogos en el módulo de citas. */
@Data
public class PersonalDTO {
    private Integer idPersonal;
    private String nombreCompleto;
    private String cargo;
    private String estado;
    private String numeroLicencia;
}
