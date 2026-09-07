package com.clinica.arches.controller;

import com.clinica.arches.dto.PacienteDTO;
import com.clinica.arches.model.Paciente;
import com.clinica.arches.service.ExportService;
import com.clinica.arches.service.FichaPdfService;
import com.clinica.arches.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes", description = "Gestión de pacientes y expedientes")
public class PacienteController {

    private final PacienteService pacienteService;
    private final ExportService exportService;
    private final FichaPdfService fichaPdfService;

    public PacienteController(PacienteService pacienteService,
                              ExportService exportService,
                              FichaPdfService fichaPdfService) {
        this.pacienteService = pacienteService;
        this.exportService = exportService;
        this.fichaPdfService = fichaPdfService;
    }

    @GetMapping
    @Operation(summary = "Listar pacientes con filtros y paginación",
            description = "Sustenta la barra de búsqueda (nombre o cédula), el filtro de estado " +
                    "(activo/inactivo) y la tabla paginada del listado de pacientes.")
    public ResponseEntity<Page<PacienteDTO>> listar(
            @Parameter(description = "Texto a buscar en nombre completo o cédula")
            @RequestParam(required = false) String search,
            @Parameter(description = "Filtra por estado del expediente: activo | inactivo")
            @RequestParam(required = false) String estado,
            @Parameter(description = "Número de página (inicia en 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Cantidad de registros por página")
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(pacienteService.listarConFiltros(search, estado, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener el detalle completo de un paciente",
            description = "Incluye datos personales, contactos de emergencia y antecedentes. " +
                    "Sustenta el botón de acción 'Ver'.")
    public ResponseEntity<PacienteDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(pacienteService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo paciente",
            description = "Sustenta el botón global 'Nuevo paciente'.")
    public ResponseEntity<PacienteDTO> crear(@RequestBody Paciente paciente) {
        return ResponseEntity.status(201).body(pacienteService.crear(paciente));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un paciente",
            description = "Sustenta el botón de acción 'Editar' (datos personales del paciente; " +
                    "contactos y antecedentes se editan con sus propios endpoints).")
    public ResponseEntity<PacienteDTO> actualizar(@PathVariable Integer id, @RequestBody Paciente paciente) {
        return ResponseEntity.ok(pacienteService.actualizar(id, paciente));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Archivar el expediente de un paciente (soft delete vía sp_archivar_paciente)",
            description = "Sustenta el botón de acción 'Eliminar'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Paciente archivado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado", content = @Content)
    })
    public ResponseEntity<Void> archivar(@PathVariable Integer id) {
        pacienteService.archivar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Exportar el listado de pacientes a Excel",
            description = "Sustenta el botón global 'Exportar Excel'. Respeta los mismos filtros " +
                    "(search, estado) que estén aplicados en la tabla.")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String estado) {
        byte[] archivo = exportService.exportarExcel(pacienteService.listarEntidadesConFiltros(search, estado));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pacientes.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(archivo);
    }

    @GetMapping("/export/pdf")
    @Operation(summary = "Exportar el listado de pacientes a PDF",
            description = "Sustenta el botón global 'Exportar PDF'. Respeta los mismos filtros " +
                    "(search, estado) que estén aplicados en la tabla.")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String estado) {
        byte[] archivo = exportService.exportarPdf(pacienteService.listarEntidadesConFiltros(search, estado));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pacientes.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(archivo);
    }

    @GetMapping("/{id}/ficha")
    @Operation(summary = "Generar la ficha individual (PDF) de un paciente",
            description = "Sustenta el botón de acción 'Imprimir'. Incluye datos personales, " +
                    "contactos de emergencia y antecedentes del paciente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ficha generada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado", content = @Content)
    })
    public ResponseEntity<byte[]> imprimirFicha(@PathVariable Integer id) {
        PacienteDTO paciente = pacienteService.buscarPorId(id);
        byte[] archivo = fichaPdfService.generarFicha(paciente);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=ficha_paciente_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(archivo);
    }
}
