package com.clinica.arches.dto;

import lombok.Data;

@Data
public class ContactoEmergenciaDTO {
    private Integer idContacto;
    private String nombreContacto;
    private String telefono;
    private String parentesco;
}
