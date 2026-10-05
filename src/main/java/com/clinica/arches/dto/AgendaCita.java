package com.clinica.arches.dto;

/** hora en formato "HH:mm". */
public record AgendaCita(
        Long id,
        String hora,
        Integer duracionMin,
        String paciente,
        String procedimiento,
        String odontologo,
        String estado) {
}

