package com.clinica.arches.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO(
        @NotNull(message = "Seleccione un usuario") Integer idUsuario,
        @NotBlank(message = "Ingrese la contraseña") String contrasena,
        boolean recordarme) {
}
