package com.clinica.arches.dto;

import lombok.Data;
import java.time.LocalDate;

/** Una fila por mes (primer día del mes). Fuente: vw_pacientes_nuevos_vs_recurrentes. */
@Data
public class PacientesNuevosVsRecurrentesDTO {
    private LocalDate mes;
    private Long pacientesNuevos;
    private Long pacientesRecurrentes;
}
