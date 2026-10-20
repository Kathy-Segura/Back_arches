package com.clinica.arches.dto;

import jakarta.validation.constraints.NotBlank;

public record CambiarContrasenaRequestDTO(
        @NotBlank(message = "Ingrese su contraseña actual") String contrasenaActual,
        @NotBlank(message = "Ingrese la nueva contraseña") String contrasenaNueva) {
}
