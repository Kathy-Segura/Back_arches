package com.clinica.arches.dto;

import lombok.Data;

/** Body opcional de PATCH /api/citas/{id}/cancelar */
@Data
public class CitaCancelarRequest {
    private Integer idMotivo; // opcional, referencia a catalogo_motivos_cancelacion
    private String notas;     // opcional, se guarda/reemplaza la nota de la cita
}
