package com.clinica.arches.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistroRequestDTO(
        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres") String nombreCompleto,
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 120, message = "El correo no puede superar 120 caracteres") String correo,
        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres") String telefono,
        @NotNull(message = "Seleccione un rol") Integer idRol,
        @NotBlank(message = "La contraseña es obligatoria") String contrasena) {
}
