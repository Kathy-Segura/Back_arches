package com.clinica.arches.config;

/** Datos de la sesión que JwtAuthFilter deja en Authentication.getDetails(). */
public record SesionInfo(Integer idRol, boolean admin, boolean debeCambiarContrasena) {
}
