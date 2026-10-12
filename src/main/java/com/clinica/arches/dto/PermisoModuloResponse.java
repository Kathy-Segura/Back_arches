package com.clinica.arches.dto;

/** Permisos de un rol sobre un módulo (false si aún no hay fila en rol_permiso). */
public record PermisoModuloResponse(
        Integer idModulo,
        String nombreModulo,
        boolean puedeVer,
        boolean puedeCrear,
        boolean puedeEditar,
        boolean puedeEliminar) {
}
