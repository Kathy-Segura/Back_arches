package com.clinica.arches.dto;

import com.clinica.arches.model.Modulo;

public record ModuloResponse(Integer idModulo, String nombreModulo) {

    public static ModuloResponse from(Modulo modulo) {
        return new ModuloResponse(modulo.getIdModulo(), modulo.getNombreModulo());
    }
}
