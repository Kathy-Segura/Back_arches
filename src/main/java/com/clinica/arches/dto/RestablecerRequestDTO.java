package com.clinica.arches.dto;

import jakarta.validation.constraints.NotBlank;

public record RestablecerRequestDTO(
        @NotBlank(message = "El enlace no es válido") String token,
        @NotBlank(message = "La contraseña es obligatoria") String contrasenaNueva) {
}
