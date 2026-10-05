package com.clinica.arches.model;

/** Se guarda como STRING. En el JSON del dashboard se expone en minúsculas (programada, no_asistio...). */
public enum EstadoCita {
    PROGRAMADA, CONFIRMADA, ATENDIDA, CANCELADA, NO_ASISTIO
}
