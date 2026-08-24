package com.clinica.arches.controller;

import com.clinica.arches.dto.PacienteDTO;
import com.clinica.arches.model.Paciente;
import com.clinica.arches.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes", description = "Gestión de pacientes y expedientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los pacientes")
    public ResponseEntity<List<PacienteDTO>> listar() {
        return ResponseEntity.ok(pacienteService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener el detalle completo de un paciente (incluye contactos y antecedentes)")
    public ResponseEntity<PacienteDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(pacienteService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo paciente")
    public ResponseEntity<PacienteDTO> crear(@RequestBody Paciente paciente) {
        return ResponseEntity.status(201).body(pacienteService.crear(paciente));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un paciente")
    public ResponseEntity<PacienteDTO> actualizar(@PathVariable Integer id, @RequestBody Paciente paciente) {
        return ResponseEntity.ok(pacienteService.actualizar(id, paciente));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Archivar el expediente de un paciente (soft delete vía sp_archivar_paciente)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Paciente archivado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado", content = @Content)
    })
    public ResponseEntity<Void> archivar(@PathVariable Integer id) {
        pacienteService.archivar(id);
        return ResponseEntity.noContent().build();
    }
}