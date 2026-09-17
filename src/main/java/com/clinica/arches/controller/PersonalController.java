package com.clinica.arches.controller;

import com.clinica.arches.dto.PersonalDTO;
import com.clinica.arches.service.PersonalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller mínimo de "personal" (odontólogos/administrativos), solo con lo
 * necesario para sustentar el módulo de citas. El módulo completo de
 * gestión de personal (CRUD, horarios, etc.) queda pendiente.
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
}
