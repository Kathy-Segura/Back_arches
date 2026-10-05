package com.clinica.arches.dto;

import java.math.BigDecimal;

public record DashboardKpis(
        long citasHoy,
        long citasHoyConfirmadas,
        long citasHoyAtendidas,
        long pacientesActivos,
        long pacientesNuevosMes,
        long tratamientosEnCurso,
        long tratamientosConSaldo,
        BigDecimal ingresosMes,
        String mesReferencia) {   // "YYYY-MM"
}