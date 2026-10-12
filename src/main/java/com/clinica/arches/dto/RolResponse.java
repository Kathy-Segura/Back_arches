package com.clinica.arches.dto;

import com.clinica.arches.model.Rol;
import java.time.LocalDateTime;

public record RolResponse(
        Integer idRol,
        String nombreRol,
        String descripcion,
        boolean activo,
        LocalDateTime fechaCreacion) {

    public static RolResponse from(Rol rol) {
        return new RolResponse(
                rol.getIdRol(),
                rol.getNombreRol(),
                rol.getDescripcion(),
                rol.isActivo(),
                rol.getFechaCreacion());
    }
}
