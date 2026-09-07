package com.clinica.arches.controller;

import com.clinica.arches.dto.AntecedenteDTO;
import com.clinica.arches.model.AntecedentePaciente;
import com.clinica.arches.service.AntecedenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes/{idPaciente}/antecedentes")
@Tag(name = "Antecedentes", description = "Antecedentes clínicos asociados a un paciente")
public class AntecedenteController {

    private final AntecedenteService antecedenteService;

    public AntecedenteController(AntecedenteService antecedenteService) {
        this.antecedenteService = antecedenteService;
    }

    @GetMapping
    @Operation(summary = "Listar los antecedentes de un paciente",
            description = "Sustenta la pestaña 'Antecedentes' dentro del botón de acción Ver del paciente")
    public ResponseEntity<List<AntecedenteDTO>> listar(@PathVariable Integer idPaciente) {
        return ResponseEntity.ok(antecedenteService.listarPorPaciente(idPaciente));
    }

    @PostMapping
    @Operation(summary = "Registrar un antecedente",
            description = "Sustenta agregar un antecedente desde la pantalla de Editar paciente")
    public ResponseEntity<AntecedenteDTO> crear(@PathVariable Integer idPaciente,
                                                @RequestBody AntecedentePaciente antecedente) {
        return ResponseEntity.status(201).body(antecedenteService.crear(idPaciente, antecedente));
    }

    @PutMapping("/{idAntecedente}")
    @Operation(summary = "Actualizar un antecedente",
            description = "Sustenta editar un antecedente existente desde la pantalla de Editar paciente")
    public ResponseEntity<AntecedenteDTO> actualizar(@PathVariable Integer idPaciente,
                                                     @PathVariable Integer idAntecedente,
                                                     @RequestBody AntecedentePaciente antecedente) {
        return ResponseEntity.ok(antecedenteService.actualizar(idPaciente, idAntecedente, antecedente));
    }

    @DeleteMapping("/{idAntecedente}")
    @Operation(summary = "Eliminar un antecedente",
            description = "Sustenta quitar un antecedente desde la pantalla de Editar paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Antecedente eliminado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Antecedente o paciente no encontrado", content = @Content)
    })
    public ResponseEntity<Void> eliminar(@PathVariable Integer idPaciente, @PathVariable Integer idAntecedente) {
        antecedenteService.eliminar(idPaciente, idAntecedente);
        return ResponseEntity.noContent().build();
    }
}
