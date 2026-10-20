package com.clinica.arches.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GuardarPermisosRequestDTO(@NotNull(message = "Faltan los permisos") @Valid List<PermisoInputDTO> permisos) {
}
