package com.clinica.arches.controller;

import com.clinica.arches.dto.*;
import com.clinica.arches.service.PermisoService;
import com.clinica.arches.service.RolService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Rutas que consume src/lib/api/roles.ts. Solo el rol administrador (ver SecurityConfig). */
@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService roles;
    private final PermisoService permisos;

    public RolController(RolService roles, PermisoService permisos) {
        this.roles = roles;
        this.permisos = permisos;
    }

    @GetMapping
    public List<RolDTO> listar(@RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return roles.listar(incluirInactivos);
    }

    @GetMapping("/{id}")
    public RolDTO obtener(@PathVariable Integer id) {
        return roles.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RolDTO crear(@Valid @RequestBody RolRequestDTO req) {
        return roles.crear(req);
    }

    @PutMapping("/{id}")
    public RolDTO actualizar(@PathVariable Integer id, @Valid @RequestBody RolRequestDTO req) {
        return roles.actualizar(id, req);
    }

    @PatchMapping("/{id}/estado")
    public RolDTO cambiarEstado(@PathVariable Integer id, @Valid @RequestBody EstadoRolRequestDTO req) {
        return roles.cambiarEstado(id, req);
    }

    @GetMapping("/{id}/permisos")
    public List<PermisoModuloDTO> obtenerPermisos(@PathVariable Integer id) {
        return permisos.matriz(id);
    }

    @PutMapping("/{id}/permisos")
    public List<PermisoModuloDTO> guardarPermisos(@PathVariable Integer id,
                                                  @Valid @RequestBody GuardarPermisosRequestDTO req) {
        return permisos.guardar(id, req);
    }
}
