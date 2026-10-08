package com.clinica.arches.dto;

import lombok.Data;
import java.time.LocalDateTime;

/** Fila del reporte "Actividad general" (bitácora). No expone datos sensibles del usuario. */
@Data
public class ReporteActividadDTO {
    private Integer idBitacora;
    private String usuario;
    private String accion;
    private String modulo;
    private LocalDateTime fechaHora;
    private String direccionIp;
}
