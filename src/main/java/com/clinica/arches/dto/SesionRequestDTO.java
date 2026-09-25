package com.clinica.arches.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SesionRequestDTO {

    /** Opcional: si la sesión viene de una cita agendada del módulo de citas. */
    private Integer idCita;

    /** Si no se envía, el service la pone en el momento actual. */
    private LocalDateTime fechaSesion;

    @NotBlank(message = "La descripción del avance es obligatoria")
    private String descripcionAvance;
}
