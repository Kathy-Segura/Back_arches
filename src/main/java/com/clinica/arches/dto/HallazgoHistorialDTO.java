package com.clinica.arches.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HallazgoHistorialDTO {
    private Integer idRegistro;
    private Integer numeroPieza;
    private Integer idHallazgoTipo;
    private String codigoHallazgo;
    private String nombreHallazgo;
    private String colorRepresentativo;
    private Integer idPersonal;
    private String nombrePersonal;
    private LocalDateTime fechaRegistro;
    private String observaciones;
}
