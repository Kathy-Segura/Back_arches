package com.clinica.arches.controller;

import com.clinica.arches.dto.*;
import com.clinica.arches.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Solo el rol administrador (ver SecurityConfig: app.security.admin-role). */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<UsuarioDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public UsuarioDTO obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO crear(@Valid @RequestBody UsuarioCrearRequestDTO req) {
        return service.crear(req);
    }

    @PutMapping("/{id}")
    public UsuarioDTO actualizar(@PathVariable Integer id,
                                 @Valid @RequestBody UsuarioActualizarRequestDTO req,
                                 @AuthenticationPrincipal Integer idActual) {
        return service.actualizar(id, req, idActual);
    }

    @PatchMapping("/{id}/aprobar")
    public UsuarioDTO aprobar(@PathVariable Integer id) {
        return service.aprobar(id);
    }

    @PutMapping("/{id}/contrasena")
    public MensajeDTO restablecerContrasena(@PathVariable Integer id,
                                            @Valid @RequestBody RestablecerContrasenaRequestDTO req) {
        return service.restablecerContrasena(id, req);
    }
}
