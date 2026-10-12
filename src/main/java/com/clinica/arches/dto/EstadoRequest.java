package com.clinica.arches.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoRequest(
        @NotNull(message = "El campo \"activo\" debe ser true o false")
        Boolean activo) {
}
