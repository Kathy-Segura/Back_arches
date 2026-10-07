package com.clinica.arches.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OdontogramaPiezaDTO {
    private Integer numeroPieza;
    private String codigoHallazgo;
    private String nombreHallazgo;
    private String colorRepresentativo;
    private LocalDateTime fechaActualizacion;
}
