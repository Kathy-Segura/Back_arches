package com.clinica.arches.controller;

import com.clinica.arches.dto.PaginaDTO;
import com.clinica.arches.dto.PersonalDTO;
import com.clinica.arches.dto.PersonalDetalleDTO;
import com.clinica.arches.dto.PersonalRequestDTO;
import com.clinica.arches.service.PersonalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
/**
 * CRUD completo de "personal" (odontólogos/administrativos). El endpoint
 * original de solo-listado (/odontologos) se mantiene igual para no romper
 * al módulo de citas.
 */
@RestController
@RequestMapping("/api/personal")
@Tag(name = "Personal", description = "Odontólogos y administrativos de la clínica")
public class PersonalController {

    private final PersonalService personalService;

    public PersonalController(PersonalService personalService) {
        this.personalService = personalService;
    }

    @GetMapping("/odontologos")
    @Operation(summary = "Listar odontólogos activos",
            description = "Sustenta el selector de odontólogo en el calendario, el listado y el formulario de citas.")
    public ResponseEntity<List<PersonalDTO>> listarOdontologos() {
        return ResponseEntity.ok(personalService.listarOdontologosActivos());
    }

    @GetMapping
    @Operation(summary = "Listar personal", description = "Listado paginado para el módulo de Personal, con filtro opcional por cargo y búsqueda por nombre.")
    public ResponseEntity<PaginaDTO<PersonalDetalleDTO>> listar(
            @RequestParam(required = false) String cargo,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombreCompleto").ascending());
        return ResponseEntity.ok(personalService.listar(cargo, q, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un registro de personal por id")
    public ResponseEntity<PersonalDetalleDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(personalService.obtener(id));
    }

    @PostMapping
    @Operation(summary = "Crear personal", description = "El estado siempre nace en 'activo'.")
    public ResponseEntity<PersonalDetalleDTO> crear(@Valid @RequestBody PersonalRequestDTO request) {
        PersonalDetalleDTO creado = personalService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar personal")
    public ResponseEntity<PersonalDetalleDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody PersonalRequestDTO request) {
        return ResponseEntity.ok(personalService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Dar de baja personal", description = "Baja lógica: pone estado = 'inactivo'. No borra el registro para no romper el historial de citas.")
    public ResponseEntity<Void> desactivar(@PathVariable Integer id) {
        personalService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
