package com.clinica.arches.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExpedienteResumenDTO {
    private Integer idPaciente;
    private String nombreCompleto;
    private Integer edad;
    private String estadoExpediente;
    private LocalDateTime historiaClinicaActualizada;
    private Long totalDiagnosticos;
    private Long totalNotasEvolucion;
    private Long totalCitasAtendidas;
}
