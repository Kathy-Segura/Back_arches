package com.clinica.arches.controller;

import com.clinica.arches.dto.CitaCancelarRequest;
import com.clinica.arches.dto.CitaDTO;
import com.clinica.arches.dto.CitaRequest;
import com.clinica.arches.dto.MotivoCancelacionDTO;
import com.clinica.arches.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
@Tag(name = "Citas", description = "Agenda y gestión de citas odontológicas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    @Operation(summary = "Listar citas con filtros y paginación",
            description = "Sustenta la pestaña 'Listado': búsqueda por paciente, filtro de odontólogo, " +
                    "estado y rango de fechas, con tabla paginada.")
    public ResponseEntity<Page<CitaDTO>> listar(
            @Parameter(description = "Texto a buscar en el nombre del paciente")
            @RequestParam(required = false) String search,
            @Parameter(description = "Filtra por id del odontólogo (personal)")
            @RequestParam(required = false) Integer idPersonal,
            @Parameter(description = "Filtra por estado: programada | confirmada | atendida | cancelada")
            @RequestParam(required = false) String estado,
            @Parameter(description = "Fecha inicial del rango (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @Parameter(description = "Fecha final del rango (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        LocalDateTime desdeDT = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime hastaDT = hasta != null ? hasta.atTime(23, 59, 59) : null;
        return ResponseEntity.ok(citaService.listarConFiltros(search, idPersonal, estado, desdeDT, hastaDT, page, size));
    }

    @GetMapping("/calendario")
    @Operation(summary = "Listar citas de un rango de fechas para la vista de calendario",
            description = "Sustenta la pestaña 'Calendario'. Devuelve todas las citas del rango sin paginar, " +
                    "opcionalmente filtradas por odontólogo.")
    public ResponseEntity<List<CitaDTO>> calendario(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer idPersonal) {
        return ResponseEntity.ok(citaService.listarParaCalendario(idPersonal, desde.atStartOfDay(), hasta.atTime(23, 59, 59)));
    }

    @GetMapping("/motivos-cancelacion")
    @Operation(summary = "Listar catálogo de motivos de cancelación",
            description = "Sustenta el selector de motivo dentro del diálogo de 'Cancelar cita'.")
    public ResponseEntity<List<MotivoCancelacionDTO>> motivosCancelacion() {
        return ResponseEntity.ok(citaService.listarMotivosCancelacion());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener el detalle de una cita", description = "Sustenta el botón de acción 'Ver'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Cita no encontrada", content = @Content)
    })
    public ResponseEntity<CitaDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(citaService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Agendar una nueva cita", description = "Sustenta el botón global 'Nueva cita'.")
    public ResponseEntity<CitaDTO> crear(@RequestBody CitaRequest request) {
        return ResponseEntity.status(201).body(citaService.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar una cita", description = "Sustenta el botón de acción 'Editar'.")
    public ResponseEntity<CitaDTO> actualizar(@PathVariable Integer id, @RequestBody CitaRequest request) {
        return ResponseEntity.ok(citaService.actualizar(id, request));
    }

    @PatchMapping("/{id}/atender")
    @Operation(summary = "Marcar una cita como atendida", description = "Sustenta el botón de acción 'Atendida'.")
    public ResponseEntity<CitaDTO> atender(@PathVariable Integer id) {
        return ResponseEntity.ok(citaService.marcarAtendida(id));
    }

    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar una cita", description = "Cambia el estado de 'programada' a 'confirmada'.")
    public ResponseEntity<CitaDTO> confirmar(@PathVariable Integer id) {
        return ResponseEntity.ok(citaService.confirmar(id));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una cita", description = "Sustenta el botón de acción 'Cancelada'. " +
            "Body opcional con idMotivo y/o notas.")
    public ResponseEntity<CitaDTO> cancelar(@PathVariable Integer id,
                                            @RequestBody(required = false) CitaCancelarRequest request) {
        return ResponseEntity.ok(citaService.cancelar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una cita", description = "Sustenta el botón de acción 'Eliminar'. Borrado físico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cita eliminada exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada", content = @Content)
    })
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        citaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
