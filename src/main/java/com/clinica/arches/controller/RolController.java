package com.clinica.arches.controller;

import com.clinica.arches.dto.EstadoRequest;
import com.clinica.arches.dto.ModuloResponse;
import com.clinica.arches.dto.PermisoModuloResponse;
import com.clinica.arches.dto.PermisosRequest;
import com.clinica.arches.dto.RolRequest;
import com.clinica.arches.dto.RolResponse;
import com.clinica.arches.service.PermisoService;
import com.clinica.arches.service.RolService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RolController {

    private final RolService rolService;
    private final PermisoService permisoService;

    public RolController(RolService rolService, PermisoService permisoService) {
        this.rolService = rolService;
        this.permisoService = permisoService;
    }

    // TODO (módulo Login): restringir con @PreAuthorize al permiso "Roles y Permisos"

    @GetMapping("/modulos")
    public List<ModuloResponse> listarModulos() {
        return permisoService.listarModulos();
    }

    @GetMapping("/roles")
    public List<RolResponse> listar(@RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return rolService.listar(incluirInactivos);
    }

    @GetMapping("/roles/{id}")
    public RolResponse obtener(@PathVariable Integer id) {
        return rolService.obtener(id);
    }

    @PostMapping("/roles")
    @ResponseStatus(HttpStatus.CREATED)
    public RolResponse crear(@Valid @RequestBody RolRequest request) {
        return rolService.crear(request);
    }

    @PutMapping("/roles/{id}")
    public RolResponse actualizar(@PathVariable Integer id, @Valid @RequestBody RolRequest request) {
        return rolService.actualizar(id, request);
    }

    @PatchMapping("/roles/{id}/estado")
    public RolResponse cambiarEstado(@PathVariable Integer id, @Valid @RequestBody EstadoRequest request) {
        return rolService.cambiarEstado(id, request.activo());
    }

    @GetMapping("/roles/{id}/permisos")
    public List<PermisoModuloResponse> obtenerPermisos(@PathVariable Integer id) {
        return permisoService.obtenerPorRol(id);
    }

    @PutMapping("/roles/{id}/permisos")
    public List<PermisoModuloResponse> guardarPermisos(
            @PathVariable Integer id, @Valid @RequestBody PermisosRequest request) {
        return permisoService.guardarPorRol(id, request);
    }
}
