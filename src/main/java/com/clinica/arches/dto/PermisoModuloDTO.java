package com.clinica.arches.dto;

/** Mismo contrato que PermisoModuloDTO de src/types/rol.ts. */
public record PermisoModuloDTO(
        Integer idModulo,
        String nombreModulo,
        boolean puedeVer,
        boolean puedeCrear,
        boolean puedeEditar,
        boolean puedeEliminar) {
}
