package com.clinica.arches.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Body de entrada para POST /api/citas y PUT /api/citas/{id}.
 * A diferencia de PacienteController (que recibe la entidad Paciente
 * directamente porque no tiene relaciones), Cita sí tiene relaciones
 * (@ManyToOne a Paciente, Personal y CatalogoProcedimiento). Recibir la
 * entidad completa obligaría al front a mandar objetos anidados; en vez de
 * eso se usa este DTO plano con solo los IDs, y CitaService se encarga de
 * resolverlos contra sus repositorios.
 */
@Data
public class CitaRequest {
    private Integer idPaciente;
    private Integer idPersonal;
    private Integer idProcedimiento; // opcional
    private LocalDateTime fechaHora;
    private Integer duracionMinutos; // opcional, default 30 si viene null
    private String notas;
}
