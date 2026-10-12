package com.clinica.arches.dto;

import jakarta.validation.constraints.NotNull;

public record PermisoRequest(
        @NotNull(message = "Cada permiso debe indicar idModulo") Integer idModulo,
        @NotNull(message = "puedeVer debe ser true o false") Boolean puedeVer,
        @NotNull(message = "puedeCrear debe ser true o false") Boolean puedeCrear,
        @NotNull(message = "puedeEditar debe ser true o false") Boolean puedeEditar,
        @NotNull(message = "puedeEliminar debe ser true o false") Boolean puedeEliminar) {
}
