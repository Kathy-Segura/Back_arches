package com.clinica.arches.dto;

import java.time.Instant;
import java.util.List;

public record LoginResponseDTO(
        String token,
        Instant expiraEn,
        UsuarioSesionDTO usuario,
        List<PermisoModuloDTO> permisos,
        boolean debeCambiarContrasena) {
}
