package com.clinica.arches.controller;

import com.clinica.arches.dto.CargaOdontologoDTO;
import com.clinica.arches.dto.CitaDTO;
import com.clinica.arches.dto.PacientesNuevosVsRecurrentesDTO;
import com.clinica.arches.dto.ReporteActividadDTO;
import com.clinica.arches.dto.ReporteProcedimientoDTO;
import com.clinica.arches.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Reportes operativos y gráficos de tendencia de la clínica")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/citas")
    @Operation(summary = "Reporte de citas por periodo y estado",
            description = "Sin paginar, orden cronológico. Estado: programada | confirmada | atendida | cancelada.")
    public ResponseEntity<List<CitaDTO>> citas(
            @Parameter(description = "Fecha inicial (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @Parameter(description = "Fecha final (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @Parameter(description = "programada | confirmada | atendida | cancelada")
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(reporteService.reporteCitas(desde, hasta, estado));
    }

    @GetMapping("/procedimientos")
    @Operation(summary = "Reporte de procedimientos (tratamientos) por periodo y avance",
            description = "El rango se aplica sobre fecha_programada. Estado: propuesto | pendiente | en_proceso | completado.")
    public ResponseEntity<List<ReporteProcedimientoDTO>> procedimientos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @Parameter(description = "propuesto | pendiente | en_proceso | completado")
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(reporteService.reporteProcedimientos(desde, hasta, estado));
    }

    @GetMapping("/actividad")
    @Operation(summary = "Reporte de actividad general (bitácora de usuarios)",
            description = "Solo registra actividad cuando la sesión de BD define app.usuario_actual_id.")
    public ResponseEntity<List<ReporteActividadDTO>> actividad(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reporteService.reporteActividad(desde, hasta));
    }

    @GetMapping("/graficos/pacientes-nuevos-vs-recurrentes")
    @Operation(summary = "Pacientes nuevos vs recurrentes por mes (histórico, citas no canceladas)")
    public ResponseEntity<List<PacientesNuevosVsRecurrentesDTO>> pacientesNuevosVsRecurrentes() {
        return ResponseEntity.ok(reporteService.pacientesNuevosVsRecurrentes());
    }

    @GetMapping("/graficos/carga-odontologos")
    @Operation(summary = "Carga de trabajo por odontólogo: citas y horas (histórico, citas no canceladas)")
    public ResponseEntity<List<CargaOdontologoDTO>> cargaOdontologos() {
        return ResponseEntity.ok(reporteService.cargaOdontologos());
    }
}
