package com.clinica.arches.repository;

/** Proyección: conteo agrupado por año/mes. */
public interface MesTotalRow {
    Integer getAnio();
    Integer getMes();
    Long getTotal();
}
