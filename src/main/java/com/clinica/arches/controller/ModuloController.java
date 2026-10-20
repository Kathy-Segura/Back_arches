package com.clinica.arches.controller;

import com.clinica.arches.dto.ModuloDTO;
import com.clinica.arches.service.PermisoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Catálogo de módulos del sistema (solo lectura). Solo el rol administrador. */
@RestController
@RequestMapping("/api/modulos")
public class ModuloController {

    private final PermisoService permisos;

    public ModuloController(PermisoService permisos) {
        this.permisos = permisos;
    }

    @GetMapping
    public List<ModuloDTO> listar() {
        return permisos.listarModulos();
    }
}
