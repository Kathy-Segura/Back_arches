package com.clinica.arches.service;

import com.clinica.arches.dto.*;
import com.clinica.arches.model.PreferenciaUsuario;
import com.clinica.arches.repository.PerfilRepository;
import com.clinica.arches.repository.PreferenciaUsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final PreferenciaUsuarioRepository preferenciaRepository;
    private final PasswordEncoder passwordEncoder;

    public PerfilService(PerfilRepository perfilRepository,
                         PreferenciaUsuarioRepository preferenciaRepository,
                         PasswordEncoder passwordEncoder) {
        this.perfilRepository = perfilRepository;
        this.preferenciaRepository = preferenciaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** SUPUESTO: el nombre de la autenticación es el correo. Si en tu JWT es el id, cambia solo este método. */
  /* public Integer resolverIdUsuario(String nombreAutenticado) {
        return perfilRepository.buscarIdUsuarioPorCorreo(nombreAutenticado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión no válida"));
    }*/

    public Integer resolverIdUsuario(String nombreAutenticado) {
        if (nombreAutenticado != null && nombreAutenticado.matches("\\d+")) {
            return Integer.valueOf(nombreAutenticado);   // el JWT guarda el id
        }
        return perfilRepository.buscarIdUsuarioPorCorreo(nombreAutenticado)   // el JWT guarda el correo
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión no válida"));
    }
    @Transactional(readOnly = true)
    public PerfilDTO obtener(Integer idUsuario) {
        return perfilRepository.buscarPerfil(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @Transactional
    public PerfilDTO actualizar(Integer idUsuario, ActualizarPerfilRequest req) {
        String correo = req.correo().trim();
        if (perfilRepository.correoEnUsoPorOtro(correo, idUsuario)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya está en uso por otro usuario");
        }
        String nombre = req.nombreCompleto().trim();
        String telefono = req.telefono() == null || req.telefono().isBlank() ? null : req.telefono().trim();
        perfilRepository.actualizarUsuario(idUsuario, nombre, correo, telefono);
        perfilRepository.actualizarPersonalVinculado(idUsuario, nombre, correo, telefono);
        return obtener(idUsuario);
    }

    @Transactional
    public PerfilDTO actualizarPreferencias(Integer idUsuario, PreferenciasRequest req) {
        PreferenciaUsuario pref = preferenciaRepository.findById(idUsuario)
                .orElseGet(() -> new PreferenciaUsuario(idUsuario));
        pref.setNotificacionesCorreo(req.notificacionesCorreo());
        preferenciaRepository.save(pref);
        return obtener(idUsuario);
    }

    @Transactional
    public PerfilDTO actualizarFoto(Integer idUsuario, FotoRequest req) {
        String foto = req.foto();
        if (foto != null && !foto.startsWith("data:image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La foto debe ser una imagen");
        }
        PreferenciaUsuario pref = preferenciaRepository.findById(idUsuario)
                .orElseGet(() -> new PreferenciaUsuario(idUsuario));
        pref.setFoto(foto);
        preferenciaRepository.save(pref);
        return obtener(idUsuario);
    }

    @Transactional
    public void cambiarContrasena(Integer idUsuario, CambiarContrasenaRequest req) {
        if (!req.contrasenaNueva().equals(req.confirmarContrasena())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La confirmación no coincide con la nueva contraseña");
        }
        String hashActual = perfilRepository.buscarHash(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (!passwordEncoder.matches(req.contrasenaActual(), hashActual)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta");
        }
        if (passwordEncoder.matches(req.contrasenaNueva(), hashActual)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva contraseña debe ser distinta a la actual");
        }
        perfilRepository.actualizarContrasena(idUsuario, passwordEncoder.encode(req.contrasenaNueva()));
    }

    @Transactional(readOnly = true)
    public List<ActividadDTO> actividad(Integer idUsuario, int limite) {
        return perfilRepository.listarActividad(idUsuario, Math.max(1, Math.min(limite, 50)));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("message", ex.getReason() != null ? ex.getReason() : "Error"));
    }
}
