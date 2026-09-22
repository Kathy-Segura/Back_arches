package com.clinica.arches.controller;

import com.clinica.arches.dto.PaginaDTO;
import com.clinica.arches.dto.ProcedimientoDTO;
import com.clinica.arches.dto.ProcedimientoRequestDTO;
import com.clinica.arches.service.ProcedimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Controller del catálogo de procedimientos.
 * Sigue el mismo patrón que PersonalController: el GET raíz devuelve la
 * lista simple de procedimientos activos (consumida por
 * `listarProcedimientos()` en el frontend, usada por el selector de
 * procedimiento en Citas) y el listado paginado para el módulo
 * administrativo vive en /buscar (consumido por `buscarProcedimientos(...)`).
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

    @GetMapping("/buscar")
    @Operation(summary = "Listar procedimientos (paginado)",
            description = "Listado paginado para el módulo de Procedimientos, con filtro opcional por categoría y búsqueda por nombre.")
    public ResponseEntity<PaginaDTO<ProcedimientoDTO>> buscar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombreProcedimiento").ascending());
        return ResponseEntity.ok(procedimientoService.listar(categoria, q, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un procedimiento", description = "Detalle de un procedimiento por su id.")
    public ResponseEntity<ProcedimientoDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(procedimientoService.obtener(id));
    }

    @PostMapping
    @Operation(summary = "Crear procedimiento", description = "Crea un procedimiento nuevo con estado 'activo'.")
    public ResponseEntity<ProcedimientoDTO> crear(@Valid @RequestBody ProcedimientoRequestDTO datos) {
        return ResponseEntity.ok(procedimientoService.crear(datos));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar procedimiento", description = "Actualiza los datos de un procedimiento existente.")
    public ResponseEntity<ProcedimientoDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody ProcedimientoRequestDTO datos) {
        return ResponseEntity.ok(procedimientoService.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar procedimiento", description = "Baja lógica (estado = 'inactivo'); no borra el registro.")
    public ResponseEntity<Void> desactivar(@PathVariable Integer id) {
        procedimientoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}

