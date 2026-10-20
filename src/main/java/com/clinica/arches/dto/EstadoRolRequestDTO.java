package com.clinica.arches.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoRolRequestDTO(@NotNull(message = "Indique si el rol está activo") Boolean activo) {
}
