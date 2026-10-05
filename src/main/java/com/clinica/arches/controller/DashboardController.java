package com.clinica.arches.controller;

import com.clinica.arches.dto.AgendaCita;
import com.clinica.arches.dto.CargaOdontologo;
import com.clinica.arches.dto.CitaMes;
import com.clinica.arches.dto.DashboardKpis;
import com.clinica.arches.dto.DashboardResumen;
import com.clinica.arches.dto.EstadoCitaTotal;
import com.clinica.arches.dto.IngresoGastoMes;
import com.clinica.arches.dto.PacientesNuevosRecurrentes;
import com.clinica.arches.dto.ProcedimientoFrecuente;
import com.clinica.arches.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;

/**
 * Endpoints del dashboard. Las rutas y los nombres de parámetros coinciden con
 * src/lib/api/dashboard.ts del front. Los valores fuera de rango los corrige
 * DashboardService (clamp), por eso aquí solo se definen los valores por defecto.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    // Deben ser literales para poder usarse en las anotaciones.
    private static final String MESES_DEFAULT = "12";          // = DashboardService.MESES_DEFAULT
    private static final String PROCEDIMIENTOS_DEFAULT = "6";
    private static final String ODONTOLOGOS_DEFAULT = "8";

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /** Todo el dashboard en una sola petición. */
    @GetMapping
    public DashboardResumen getResumen(
            @RequestParam(name = "meses", defaultValue = MESES_DEFAULT) int meses,
            @RequestParam(name = "procedimientos", defaultValue = PROCEDIMIENTOS_DEFAULT) int procedimientos,
            @RequestParam(name = "odontologos", defaultValue = ODONTOLOGOS_DEFAULT) int odontologos) {
        return dashboardService.getResumen(meses, procedimientos, odontologos);
    }

    @GetMapping("/kpis")
    public DashboardKpis getKpis() {
        return dashboardService.getKpis();
    }

    @GetMapping("/citas-por-mes")
    public List<CitaMes> getCitasPorMes(
            @RequestParam(name = "meses", defaultValue = MESES_DEFAULT) int meses) {
        return dashboardService.getCitasPorMes(meses);
    }

    @GetMapping("/procedimientos-frecuentes")
    public List<ProcedimientoFrecuente> getProcedimientosFrecuentes(
            @RequestParam(name = "procedimientos", defaultValue = PROCEDIMIENTOS_DEFAULT) int procedimientos,
            @RequestParam(name = "meses", defaultValue = MESES_DEFAULT) int meses) {
        return dashboardService.getProcedimientosFrecuentes(procedimientos, meses);
    }

    @GetMapping("/ingresos-vs-gastos")
    public List<IngresoGastoMes> getIngresosVsGastos(
            @RequestParam(name = "meses", defaultValue = MESES_DEFAULT) int meses) {
        return dashboardService.getIngresosVsGastos(meses);
    }

    @GetMapping("/estados-citas")
    public List<EstadoCitaTotal> getEstadosCitas(
            @RequestParam(name = "meses", defaultValue = MESES_DEFAULT) int meses) {
        return dashboardService.getEstadosCitas(meses);
    }

    @GetMapping("/agenda-hoy")
    public List<AgendaCita> getAgendaHoy() {
        return dashboardService.getAgendaHoy();
    }

    @GetMapping("/carga-odontologos")
    public List<CargaOdontologo> getCargaOdontologos(
            @RequestParam(name = "odontologos", defaultValue = ODONTOLOGOS_DEFAULT) int odontologos,
            @RequestParam(name = "meses", defaultValue = MESES_DEFAULT) int meses) {
        return dashboardService.getCargaOdontologos(odontologos, meses);
    }

    @GetMapping("/pacientes-nuevos-recurrentes")
    public List<PacientesNuevosRecurrentes> getPacientesNuevosRecurrentes(
            @RequestParam(name = "meses", defaultValue = MESES_DEFAULT) int meses) {
        return dashboardService.getPacientesNuevosRecurrentes(meses);
    }
}
