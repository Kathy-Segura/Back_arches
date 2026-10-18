package com.clinica.arches.dto;

import com.clinica.arches.model.Rol;

import java.time.LocalDateTime;

/** Mismo contrato que RolDTO de src/types/rol.ts. */
public record RolDTO(Integer idRol, String nombreRol, String descripcion, Boolean activo, LocalDateTime fechaCreacion) {

    public static RolDTO from(Rol r) {
        return new RolDTO(r.getIdRol(), r.getNombreRol(), r.getDescripcion(), r.isActivo(), r.getFechaCreacion());
    }
}
