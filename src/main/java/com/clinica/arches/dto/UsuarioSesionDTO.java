package com.clinica.arches.dto;

import com.clinica.arches.model.Usuario;

public record UsuarioSesionDTO(
        Integer idUsuario,
        String nombreCompleto,
        String correo,
        Integer idRol,
        String nombreRol) {

    public static UsuarioSesionDTO from(Usuario u) {
        return new UsuarioSesionDTO(u.getIdUsuario(), u.getNombreCompleto(), u.getCorreo(),
                u.getRol().getIdRol(), u.getRol().getNombreRol());
    }
}
