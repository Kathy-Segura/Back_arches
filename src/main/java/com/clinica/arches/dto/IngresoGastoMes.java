package com.clinica.arches.dto;

import java.math.BigDecimal;

/** mes: "YYYY-MM" */
public record IngresoGastoMes(String mes, BigDecimal ingresos, BigDecimal gastos) {
}
