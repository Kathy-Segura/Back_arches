package com.clinica.arches.dto;

import jakarta.validation.constraints.NotBlank;

/** RestablecerContrasenaInput del front. */
public record RestablecerContrasenaRequestDTO(
        @NotBlank(message = "La contraseña temporal es obligatoria") String contrasenaTemporal) {
}
