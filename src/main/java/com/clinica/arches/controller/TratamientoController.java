package com.clinica.arches.controller;

import com.clinica.arches.dto.PaginaDTO;
import com.clinica.arches.dto.SesionRequestDTO;
import com.clinica.arches.dto.TratamientoDTO;
import com.clinica.arches.dto.TratamientoRequestDTO;
import com.clinica.arches.service.TratamientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tratamientos")
@Tag(name = "Tratamientos", description = "Tratamientos asignados a pacientes y su avance/pago")
public class TratamientoController {

    private final TratamientoService tratamientoService;

    public TratamientoController(TratamientoService tratamientoService) {
        this.tratamientoService = tratamientoService;
    }

    @GetMapping
    @Operation(summary = "Listar tratamientos", description = "Paginado, con filtro opcional por paciente, odontólogo, avance y pago.")
    public ResponseEntity<PaginaDTO<TratamientoDTO>> listar(
            @RequestParam(required = false) Integer idPaciente,
            @RequestParam(required = false) Integer idPersonal,
            @RequestParam(required = false) String estadoAvance,
            @RequestParam(required = false) String estadoPago,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        return ResponseEntity.ok(tratamientoService.listar(idPaciente, idPersonal, estadoAvance, estadoPago, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un tratamiento con su historial de sesiones")
    public ResponseEntity<TratamientoDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(tratamientoService.obtener(id));
    }

    @PostMapping
    @Operation(summary = "Asignar tratamiento", description = "Nace con estadoAvance = 'propuesto' y estadoPago = 'pendiente'.")
    public ResponseEntity<TratamientoDTO> crear(@Valid @RequestBody TratamientoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tratamientoService.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tratamiento", description = "Permite cambiar estadoAvance/estadoPago manualmente además de los datos base.")
    public ResponseEntity<TratamientoDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody TratamientoRequestDTO request) {
        return ResponseEntity.ok(tratamientoService.actualizar(id, request));
    }

    @PostMapping("/{id}/sesiones")
    @Operation(summary = "Registrar sesión de avance", description = "Inserta la sesión; el avance del tratamiento lo recalcula el trigger de base de datos.")
    public ResponseEntity<TratamientoDTO> registrarSesion(@PathVariable Integer id, @Valid @RequestBody SesionRequestDTO request) {
        return ResponseEntity.ok(tratamientoService.registrarSesion(id, request));
    }
}
