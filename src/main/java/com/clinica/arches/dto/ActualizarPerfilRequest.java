package com.clinica.arches.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarPerfilRequest(
        @NotBlank @Size(max = 150) String nombreCompleto,
        @NotBlank @Email @Size(max = 120) String correo,
        @Size(max = 20) String telefono
) {}
