package com.clinica.arches.dto;

import java.util.List;

public record SesionActivaDTO(
        UsuarioSesionDTO usuario,
        List<PermisoModuloDTO> permisos,
        boolean debeCambiarContrasena) {
}