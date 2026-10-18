package com.clinica.arches.controller;

import com.clinica.arches.dto.*;
import com.clinica.arches.exception.ApiException;
import com.clinica.arches.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    /** Combo del login: id y nombre de usuarios activos. */
    @GetMapping("/usuarios")
    public List<UsuarioLoginItemDTO> usuarios() {
        return service.usuariosParaLogin();
    }

    /** Combo del registro: roles activos. */
    @GetMapping("/roles")
    public List<RolDTO> roles() {
        return service.rolesParaRegistro();
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO req) {
        return service.login(req);
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public MensajeDTO registro(@Valid @RequestBody RegistroRequestDTO req) {
        return service.registrar(req);
    }

    @PostMapping("/recuperar")
    public MensajeDTO recuperar(@Valid @RequestBody RecuperarRequestDTO req) {
        return service.recuperar(req);
    }

    @PostMapping("/restablecer")
    public MensajeDTO restablecer(@Valid @RequestBody RestablecerRequestDTO req) {
        return service.restablecer(req);
    }

    /** Sesión actual (requiere token). */
    @GetMapping("/me")
    public UsuarioSesionDTO me(@AuthenticationPrincipal Integer idUsuario) {
        if (idUsuario == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Debe iniciar sesión");
        }
        return service.sesion(idUsuario);
    }
}
