package com.clinica.arches.dto;

import java.util.List;

public record DashboardResumen(
        DashboardKpis kpis,
        List<CitaMes> citasPorMes,
        List<ProcedimientoFrecuente> procedimientosFrecuentes,
        List<IngresoGastoMes> ingresosVsGastos,
        List<EstadoCitaTotal> estadosCitas,
        List<AgendaCita> agendaHoy,
        List<CargaOdontologo> cargaOdontologos,
        List<PacientesNuevosRecurrentes> pacientesNuevosRecurrentes) {
}
