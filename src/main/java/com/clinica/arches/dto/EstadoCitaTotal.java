package com.clinica.arches.dto;

/** estado en minúsculas: programada | confirmada | atendida | cancelada | no_asistio */
public record EstadoCitaTotal(String estado, long total) {
}
