package com.clinica.arches.controller;

import com.clinica.arches.dto.ProcedimientoDTO;
import com.clinica.arches.service.ProcedimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller mínimo del catálogo de procedimientos, solo con lo necesario
 * para sustentar el selector de procedimiento en el módulo de citas.
 */
@RestController
@RequestMapping("/api/procedimientos")
@Tag(name = "Procedimientos", description = "Catálogo de procedimientos odontológicos")
public class ProcedimientoController {

    private final ProcedimientoService procedimientoService;

    public ProcedimientoController(ProcedimientoService procedimientoService) {
        this.procedimientoService = procedimientoService;
    }

    @GetMapping
    @Operation(summary = "Listar procedimientos activos",
            description = "Sustenta el selector de procedimiento en el formulario de citas.")
    public ResponseEntity<List<ProcedimientoDTO>> listar() {
        return ResponseEntity.ok(procedimientoService.listarActivos());
    }
}
