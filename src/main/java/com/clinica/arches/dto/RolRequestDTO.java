package com.clinica.arches.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** RolInput del front. */
public record RolRequestDTO(
        @NotBlank(message = "El nombre del rol es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar 50 caracteres") String nombreRol,
        @Size(max = 200, message = "La descripción no puede superar 200 caracteres") String descripcion) {
}
