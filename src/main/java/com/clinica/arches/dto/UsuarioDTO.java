package com.clinica.arches.dto;

import com.clinica.arches.model.Rol;
import com.clinica.arches.model.Usuario;

import java.time.LocalDateTime;

/** Mismo contrato que UsuarioDTO de src/types/usuario.ts. estado: activo | inactivo | pendiente. */
public record UsuarioDTO(
        Integer idUsuario,
        String nombreCompleto,
        String correo,
        String telefono,
        Integer idRol,
        String nombreRol,
        String estado,
        LocalDateTime ultimoAcceso,
        LocalDateTime fechaCreacion) {

    public static UsuarioDTO from(Usuario u) {
        Rol r = u.getRol();
        return new UsuarioDTO(u.getIdUsuario(), u.getNombreCompleto(), u.getCorreo(), u.getTelefono(),
                r.getIdRol(), r.getNombreRol(), u.getEstado(), u.getUltimoAcceso(), u.getFechaCreacion());
    }
}
