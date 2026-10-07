package com.clinica.arches.dto;

import lombok.Data;

@Data
public class HallazgoRegistroRequest {
    private Integer idHallazgoTipo;
    private Integer idPersonal;
    private String observaciones;
}
