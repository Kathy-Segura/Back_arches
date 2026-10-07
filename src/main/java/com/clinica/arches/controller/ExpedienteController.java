package com.clinica.arches.controller;

import com.clinica.arches.dto.*;
import com.clinica.arches.service.ExpedienteService;
import com.clinica.arches.service.PersonalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expedientes")
@Tag(name = "Expedientes", description = "Expediente clínico: historia clínica, odontograma, diagnósticos, plan de tratamiento y evolución")
public class ExpedienteController {

    private final ExpedienteService expedienteService;
    private final PersonalService personalService;

    public ExpedienteController(ExpedienteService expedienteService, PersonalService personalService) {
        this.expedienteService = expedienteService;
        this.personalService = personalService;
    }

    /* -------- Catálogos de apoyo (no dependen del paciente) -------- */

    @GetMapping("/odontologos")
    @Operation(summary = "Odontólogos activos", description = "Selector de autor para diagnósticos, notas y hallazgos.")
    public ResponseEntity<List<PersonalDTO>> odontologos() {
        return ResponseEntity.ok(personalService.listarOdontologosActivos());
    }

    @GetMapping("/hallazgos-tipos")
    @Operation(summary = "Catálogo de hallazgos dentales", description = "Tipos de hallazgo que se pueden registrar en el odontograma.")
    public ResponseEntity<List<HallazgoTipoDTO>> tiposHallazgo() {
        return ResponseEntity.ok(expedienteService.listarTiposHallazgo());
    }

    /* ----------------------------- Resumen ----------------------------- */

    @GetMapping("/{idPaciente}")
    @Operation(summary = "Resumen del expediente", description = "Totales de diagnósticos, notas y citas atendidas (vw_expediente_resumen).")
    public ResponseEntity<ExpedienteResumenDTO> resumen(@PathVariable Integer idPaciente) {
        return ResponseEntity.ok(expedienteService.obtenerResumen(idPaciente));
    }

    /* ------------------------- Historia clínica ------------------------- */

    @GetMapping("/{idPaciente}/historia-clinica")
    @Operation(summary = "Obtener la historia clínica", description = "Si el paciente aún no tiene historia devuelve los campos vacíos.")
    public ResponseEntity<HistoriaClinicaDTO> historia(@PathVariable Integer idPaciente) {
        return ResponseEntity.ok(expedienteService.obtenerHistoria(idPaciente));
    }

    @PutMapping("/{idPaciente}/historia-clinica")
    @Operation(summary = "Guardar la historia clínica", description = "Crea o actualiza la historia única del paciente.")
    public ResponseEntity<HistoriaClinicaDTO> guardarHistoria(@PathVariable Integer idPaciente,
                                                              @RequestBody HistoriaClinicaRequest request) {
        return ResponseEntity.ok(expedienteService.guardarHistoria(idPaciente, request));
    }

    /* ------------------------------ Odontograma ------------------------------ */

    @GetMapping("/{idPaciente}/odontograma")
    @Operation(summary = "Odontograma actual", description = "Hallazgo vigente de cada pieza que tiene registro.")
    public ResponseEntity<List<OdontogramaPiezaDTO>> odontograma(@PathVariable Integer idPaciente) {
        return ResponseEntity.ok(expedienteService.odontogramaActual(idPaciente));
    }

    @GetMapping("/{idPaciente}/odontograma/{numeroPieza}/historial")
    @Operation(summary = "Historial de hallazgos de una pieza")
    public ResponseEntity<List<HallazgoHistorialDTO>> historialPieza(@PathVariable Integer idPaciente,
                                                                     @PathVariable Integer numeroPieza) {
        return ResponseEntity.ok(expedienteService.historialPieza(idPaciente, numeroPieza));
    }

    @PostMapping("/{idPaciente}/odontograma/{numeroPieza}/hallazgos")
    @Operation(summary = "Registrar un hallazgo en una pieza", description = "Numeración FDI: 11-18, 21-28, 31-38, 41-48.")
    public ResponseEntity<OdontogramaPiezaDTO> registrarHallazgo(@PathVariable Integer idPaciente,
                                                                 @PathVariable Integer numeroPieza,
                                                                 @RequestBody HallazgoRegistroRequest request) {
        return ResponseEntity.status(201).body(expedienteService.registrarHallazgo(idPaciente, numeroPieza, request));
    }

    /* ----------------------------- Diagnósticos ----------------------------- */

    @GetMapping("/{idPaciente}/diagnosticos")
    @Operation(summary = "Listar diagnósticos del paciente")
    public ResponseEntity<List<DiagnosticoDTO>> diagnosticos(@PathVariable Integer idPaciente) {
        return ResponseEntity.ok(expedienteService.listarDiagnosticos(idPaciente));
    }

    @PostMapping("/{idPaciente}/diagnosticos")
    @Operation(summary = "Registrar un diagnóstico")
    public ResponseEntity<DiagnosticoDTO> crearDiagnostico(@PathVariable Integer idPaciente,
                                                           @RequestBody DiagnosticoRequest request) {
        return ResponseEntity.status(201).body(expedienteService.crearDiagnostico(idPaciente, request));
    }

    @PutMapping("/{idPaciente}/diagnosticos/{idDiagnostico}")
    @Operation(summary = "Editar un diagnóstico")
    public ResponseEntity<DiagnosticoDTO> actualizarDiagnostico(@PathVariable Integer idPaciente,
                                                                @PathVariable Integer idDiagnostico,
                                                                @RequestBody DiagnosticoRequest request) {
        return ResponseEntity.ok(expedienteService.actualizarDiagnostico(idPaciente, idDiagnostico, request));
    }

    @DeleteMapping("/{idPaciente}/diagnosticos/{idDiagnostico}")
    @Operation(summary = "Eliminar un diagnóstico", description = "Borrado físico.")
    public ResponseEntity<Void> eliminarDiagnostico(@PathVariable Integer idPaciente,
                                                    @PathVariable Integer idDiagnostico) {
        expedienteService.eliminarDiagnostico(idPaciente, idDiagnostico);
        return ResponseEntity.noContent().build();
    }

    /* ------------------------- Plan de tratamiento ------------------------- */

    @GetMapping("/{idPaciente}/plan-tratamiento")
    @Operation(summary = "Plan de tratamiento del paciente", description = "Solo lectura; se administra en el módulo Tratamientos.")
    public ResponseEntity<List<PlanTratamientoDTO>> planTratamiento(@PathVariable Integer idPaciente) {
        return ResponseEntity.ok(expedienteService.planTratamiento(idPaciente));
    }

    /* ------------------------- Evolución y notas ------------------------- */

    @GetMapping("/{idPaciente}/evolucion")
    @Operation(summary = "Bitácora de evolución del paciente")
    public ResponseEntity<List<EvolucionDTO>> evolucion(@PathVariable Integer idPaciente) {
        return ResponseEntity.ok(expedienteService.listarEvolucion(idPaciente));
    }

    @PostMapping("/{idPaciente}/evolucion")
    @Operation(summary = "Registrar una nota de evolución")
    public ResponseEntity<EvolucionDTO> crearEvolucion(@PathVariable Integer idPaciente,
                                                       @RequestBody EvolucionRequest request) {
        return ResponseEntity.status(201).body(expedienteService.crearEvolucion(idPaciente, request));
    }

    @PutMapping("/{idPaciente}/evolucion/{idEvolucion}")
    @Operation(summary = "Editar el texto de una nota", description = "Conserva el autor y la fecha originales.")
    public ResponseEntity<EvolucionDTO> actualizarEvolucion(@PathVariable Integer idPaciente,
                                                            @PathVariable Integer idEvolucion,
                                                            @RequestBody EvolucionRequest request) {
        return ResponseEntity.ok(expedienteService.actualizarEvolucion(idPaciente, idEvolucion, request));
    }

    @DeleteMapping("/{idPaciente}/evolucion/{idEvolucion}")
    @Operation(summary = "Eliminar una nota de evolución", description = "Borrado físico.")
    public ResponseEntity<Void> eliminarEvolucion(@PathVariable Integer idPaciente,
                                                  @PathVariable Integer idEvolucion) {
        expedienteService.eliminarEvolucion(idPaciente, idEvolucion);
        return ResponseEntity.noContent().build();
    }
}
