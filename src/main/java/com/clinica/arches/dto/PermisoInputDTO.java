package com.clinica.arches.dto;

import jakarta.validation.constraints.NotNull;

/** Una fila de la matriz que envía el front (PermisoInput). */
public record PermisoInputDTO(
        @NotNull(message = "Falta el módulo") Integer idModulo,
        boolean puedeVer,
        boolean puedeCrear,
        boolean puedeEditar,
        boolean puedeEliminar) {
}
