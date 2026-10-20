package com.clinica.arches.service;

import com.clinica.arches.dto.*;
import com.clinica.arches.exception.ApiException;
import com.clinica.arches.model.Rol;
import com.clinica.arches.model.Usuario;
import com.clinica.arches.repository.RolRepository;
import com.clinica.arches.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String ENLACE_INVALIDO = "El enlace de recuperación no es válido o ya expiró";

    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final LoginAttemptService intentos;
    private final MailService mail;
    private final PermisoService permisos;
    private final String frontendUrl;

    public AuthService(UsuarioRepository usuarios, RolRepository roles, PasswordEncoder encoder, JwtService jwt,
                       LoginAttemptService intentos, MailService mail, PermisoService permisos,
                       @Value("${app.frontend-url:http://localhost:8080}") String frontendUrl) {
        this.usuarios = usuarios;
        this.roles = roles;
        this.encoder = encoder;
        this.jwt = jwt;
        this.intentos = intentos;
        this.mail = mail;
        this.permisos = permisos;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
    }

    /* ---------------- combos públicos ---------------- */

    @Transactional(readOnly = true)
    public List<UsuarioLoginItemDTO> usuariosParaLogin() {
        return usuarios.findActivosParaLogin();
    }

    @Transactional(readOnly = true)
    public List<RolDTO> rolesParaRegistro() {
        return roles.findByActivoTrueOrderByNombreRol().stream().map(RolDTO::from).toList();
    }

    /* ---------------- login ---------------- */

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO req) {
        intentos.verificar(req.idUsuario());

        Usuario u = usuarios.findConRolById(req.idUsuario()).orElse(null);
        boolean credencialesOk = u != null && encoder.matches(req.contrasena(), u.getContrasenaHash());
        if (!credencialesOk) {
            intentos.registrarFallo(req.idUsuario());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
        }

        // El estado solo se revela a quien ya demostró conocer la contraseña.
        if ("pendiente".equals(u.getEstado())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Su cuenta está pendiente de aprobación por un administrador");
        }
        if (!"activo".equals(u.getEstado())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Su cuenta está inactiva. Contacte al administrador");
        }
        if (!u.getRol().isActivo()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Su rol está inactivo. Contacte al administrador");
        }

        intentos.limpiar(req.idUsuario());
        u.setUltimoAcceso(LocalDateTime.now());
        JwtService.Emitido e = jwt.crearSesion(u, req.recordarme());
        return new LoginResponseDTO(e.token(), e.expiraEn(), UsuarioSesionDTO.from(u),
                permisos.paraSesion(u.getRol()), u.isDebeCambiarContrasena());
    }

   /* @Transactional(readOnly = true)
    public SesionDTO sesion(Integer idUsuario) {
        Usuario u = usuarios.findConRolById(idUsuario)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Debe iniciar sesión"));
        return new SesionDTO(UsuarioSesionDTO.from(u), permisos.paraSesion(u.getRol()), u.isDebeCambiarContrasena());
    }*/

    @Transactional(readOnly = true)
    public SesionActivaDTO sesion(Integer idUsuario) {
        Usuario u = usuarios.findConRolById(idUsuario)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Debe iniciar sesión"));
        return new SesionActivaDTO(UsuarioSesionDTO.from(u),
                permisos.paraSesion(u.getRol()),
                u.isDebeCambiarContrasena());
    }


    /** Cambio de contraseña del propio usuario (también cierra el ciclo de la contraseña temporal). */
    @Transactional
    public MensajeDTO cambiarContrasena(Integer idUsuario, CambiarContrasenaRequestDTO req) {
        intentos.verificar(idUsuario);
        Usuario u = usuarios.findConRolById(idUsuario)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Debe iniciar sesión"));
        if (!encoder.matches(req.contrasenaActual(), u.getContrasenaHash())) {
            intentos.registrarFallo(idUsuario);
            throw new ApiException(HttpStatus.BAD_REQUEST, "La contraseña actual no es correcta");
        }
        String error = PasswordPolicy.validar(req.contrasenaNueva());
        if (error != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, error);
        }
        if (req.contrasenaNueva().equals(req.contrasenaActual())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La nueva contraseña debe ser distinta de la actual");
        }
        u.setContrasenaHash(encoder.encode(req.contrasenaNueva()));
        u.setDebeCambiarContrasena(false);
        intentos.limpiar(idUsuario);
        return new MensajeDTO("Contraseña actualizada correctamente");
    }

    /* ---------------- registro público ---------------- */

    /** La cuenta nace 'pendiente': un administrador debe aprobarla (y puede corregir el rol) desde Usuarios. */
    @Transactional
    public MensajeDTO registrar(RegistroRequestDTO req) {
        String error = PasswordPolicy.validar(req.contrasena());
        if (error != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, error);
        }
        String correo = req.correo().trim().toLowerCase(Locale.ROOT);
        if (usuarios.existsByCorreoIgnoreCase(correo)) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe un usuario registrado con este correo");
        }
        Rol rol = roles.findById(req.idRol())
                .filter(Rol::isActivo)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "El rol seleccionado no es válido"));

        Usuario u = new Usuario();
        u.setNombreCompleto(req.nombreCompleto().trim());
        u.setCorreo(correo);
        String tel = req.telefono() == null ? "" : req.telefono().trim();
        u.setTelefono(tel.isEmpty() ? null : tel);
        u.setRol(rol);
        u.setContrasenaHash(encoder.encode(req.contrasena()));
        u.setEstado("pendiente");
        try {
            usuarios.saveAndFlush(u);
        } catch (DataIntegrityViolationException ex) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe un usuario registrado con este correo");
        }
        return new MensajeDTO("Solicitud enviada. Un administrador debe aprobar su cuenta antes de que pueda iniciar sesión");
    }

    /* ---------------- recuperar / restablecer ---------------- */

    /** Responde siempre lo mismo, exista o no el correo, para no revelar qué correos están registrados. */
    @Transactional(readOnly = true)
    public MensajeDTO recuperar(RecuperarRequestDTO req) {
        usuarios.findByCorreoIgnoreCase(req.correo().trim())
                .filter(u -> "activo".equals(u.getEstado()))
                .ifPresent(u -> {
                    try {
                        String enlace = frontendUrl + "/restablecer?token=" + jwt.crearTokenRestablecer(u);
                        mail.enviarRecuperacion(u.getCorreo(), u.getNombreCompleto(), enlace);
                    } catch (RuntimeException ex) {
                        log.error("No se pudo enviar el correo de recuperación", ex);
                    }
                });
        return new MensajeDTO("Si el correo está registrado, recibirá un enlace para restablecer su contraseña");
    }

    @Transactional
    public MensajeDTO restablecer(RestablecerRequestDTO req) {
        String error = PasswordPolicy.validar(req.contrasenaNueva());
        if (error != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, error);
        }
        Claims c;
        try {
            c = jwt.leerReset(req.token());
        } catch (JwtException | IllegalArgumentException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ENLACE_INVALIDO);
        }
        Usuario u = usuarios.findById(Integer.valueOf(c.getSubject()))
                .filter(x -> "activo".equals(x.getEstado()))
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, ENLACE_INVALIDO));
        if (!jwt.huella(u).equals(c.get("ph", String.class))) { // ya se usó: el hash cambió
            throw new ApiException(HttpStatus.BAD_REQUEST, ENLACE_INVALIDO);
        }
        u.setContrasenaHash(encoder.encode(req.contrasenaNueva()));
        u.setDebeCambiarContrasena(false);
        intentos.limpiar(u.getIdUsuario());
        return new MensajeDTO("Contraseña actualizada. Ya puede iniciar sesión");
    }
}
