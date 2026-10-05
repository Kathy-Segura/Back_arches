package com.clinica.arches.repository;

import java.math.BigDecimal;

/** Proyección: suma de montos agrupada por año/mes. */
public interface MesMontoRow {
    Integer getAnio();
    Integer getMes();
    BigDecimal getTotal();
}
