package com.clinica.arches.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PermisosRequest(
        @NotNull(message = "Se espera una lista \"permisos\"")
        List<@Valid @NotNull(message = "Hay un permiso vacío en la lista") PermisoRequest> permisos) {
}
