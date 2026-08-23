package com.clinica.arches.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AntecedenteDTO {
    private Integer idAntecedente;
    private String tipoAntecedente;
    private String descripcion;
    private LocalDateTime fechaRegistro;
}
