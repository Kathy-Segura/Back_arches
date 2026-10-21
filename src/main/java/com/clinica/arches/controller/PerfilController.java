package com.clinica.arches.controller;

import com.clinica.arches.dto.*;
import com.clinica.arches.service.PerfilService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Todo opera sobre el usuario autenticado: nunca recibe un id por la URL. */
@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    private final PerfilService service;
    
    public PerfilController(PerfilService service) {
        this.service = service;
    }

   /*@GetMapping
    public PerfilDTO obtener(Authentication auth) {
        return service.obtener(service.resolverIdUsuario(auth.getName()));
    }*/

    @GetMapping
    public PerfilDTO obtener(Authentication auth) {
        System.out.println("AUTH NAME = " + auth.getName() + " | PRINCIPAL = " + auth.getPrincipal());
        return service.obtener(service.resolverIdUsuario(auth.getName()));
    }

    @PutMapping
    public PerfilDTO actualizar(Authentication auth, @Valid @RequestBody ActualizarPerfilRequest req) {
        return service.actualizar(service.resolverIdUsuario(auth.getName()), req);
    }

    @PutMapping("/preferencias")
    public PerfilDTO preferencias(Authentication auth, @Valid @RequestBody PreferenciasRequest req) {
        return service.actualizarPreferencias(service.resolverIdUsuario(auth.getName()), req);
    }

    @PutMapping("/foto")
    public PerfilDTO foto(Authentication auth, @Valid @RequestBody FotoRequest req) {
        return service.actualizarFoto(service.resolverIdUsuario(auth.getName()), req);
    }

    @PutMapping("/contrasena")
    public ResponseEntity<Void> contrasena(Authentication auth, @Valid @RequestBody CambiarContrasenaRequest req) {
        service.cambiarContrasena(service.resolverIdUsuario(auth.getName()), req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/actividad")
    public List<ActividadDTO> actividad(Authentication auth, @RequestParam(defaultValue = "10") int limite) {
        return service.actividad(service.resolverIdUsuario(auth.getName()), limite);
    }
}
