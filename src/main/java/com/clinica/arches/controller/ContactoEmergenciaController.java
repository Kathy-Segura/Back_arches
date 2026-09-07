package com.clinica.arches.controller;

import com.clinica.arches.dto.ContactoEmergenciaDTO;
import com.clinica.arches.model.ContactoEmergencia;
import com.clinica.arches.service.ContactoEmergenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes/{idPaciente}/contactos")
@Tag(name = "Contactos de Emergencia", description = "Contactos de emergencia asociados a un paciente")
public class ContactoEmergenciaController {

    private final ContactoEmergenciaService contactoService;

    public ContactoEmergenciaController(ContactoEmergenciaService contactoService) {
        this.contactoService = contactoService;
    }

    @GetMapping
    @Operation(summary = "Listar los contactos de emergencia de un paciente",
            description = "Sustenta la pestaña 'Contactos' dentro del botón de acción Ver del paciente")
    public ResponseEntity<List<ContactoEmergenciaDTO>> listar(@PathVariable Integer idPaciente) {
        return ResponseEntity.ok(contactoService.listarPorPaciente(idPaciente));
    }

    @PostMapping
    @Operation(summary = "Registrar un contacto de emergencia",
            description = "Sustenta agregar un contacto desde la pantalla de Editar paciente")
    public ResponseEntity<ContactoEmergenciaDTO> crear(@PathVariable Integer idPaciente,
                                                       @RequestBody ContactoEmergencia contacto) {
        return ResponseEntity.status(201).body(contactoService.crear(idPaciente, contacto));
    }

    @PutMapping("/{idContacto}")
    @Operation(summary = "Actualizar un contacto de emergencia",
            description = "Sustenta editar un contacto existente desde la pantalla de Editar paciente")
    public ResponseEntity<ContactoEmergenciaDTO> actualizar(@PathVariable Integer idPaciente,
                                                            @PathVariable Integer idContacto,
                                                            @RequestBody ContactoEmergencia contacto) {
        return ResponseEntity.ok(contactoService.actualizar(idPaciente, idContacto, contacto));
    }

    @DeleteMapping("/{idContacto}")
    @Operation(summary = "Eliminar un contacto de emergencia",
            description = "Sustenta quitar un contacto desde la pantalla de Editar paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contacto eliminado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Contacto o paciente no encontrado", content = @Content)
    })
    public ResponseEntity<Void> eliminar(@PathVariable Integer idPaciente, @PathVariable Integer idContacto) {
        contactoService.eliminar(idPaciente, idContacto);
        return ResponseEntity.noContent().build();
    }
}
