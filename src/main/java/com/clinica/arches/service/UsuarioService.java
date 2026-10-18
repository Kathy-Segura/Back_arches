package com.clinica.arches.service;

import com.clinica.arches.dto.*;
import com.clinica.arches.exception.ApiException;
import com.clinica.arches.model.Rol;
import com.clinica.arches.model.Usuario;
import com.clinica.arches.repository.RolRepository;
import com.clinica.arches.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Administración de usuarios. Baja lógica (estado 'inactivo'), igual que personal: no se elimina
 * ninguna fila. El acceso a estos endpoints se restringe al rol administrador en SecurityConfig.
 */
@Service
public class UsuarioService {

    private static final String CORREO_DUPLICADO = "Ya existe un usuario registrado con este correo";

    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PasswordEncoder encoder;
    private final LoginAttemptService intentos;
    private final String rolAdmin;

    @PersistenceContext
    private EntityManager em;

    public UsuarioService(UsuarioRepository usuarios, RolRepository roles, PasswordEncoder encoder,
                          LoginAttemptService intentos,
                          @Value("${app.security.admin-role:Administrador}") String rolAdmin) {
        this.usuarios = usuarios;
        this.roles = roles;
        this.encoder = encoder;
        this.intentos = intentos;
        this.rolAdmin = rolAdmin;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listar() {
        return usuarios.findAllConRol().stream().map(UsuarioDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtener(Integer id) {
        return UsuarioDTO.from(buscar(id));
    }

    @Transactional
    public UsuarioDTO crear(UsuarioCrearRequestDTO req) {
        validarContrasena(req.contrasenaTemporal());
        String correo = normalizarCorreo(req.correo());
        if (usuarios.existsByCorreoIgnoreCase(correo)) {
            throw new ApiException(HttpStatus.CONFLICT, CORREO_DUPLICADO);
        }
        Rol rol = rolActivo(req.idRol());

        Usuario u = new Usuario();
        u.setNombreCompleto(req.nombreCompleto().trim());
        u.setCorreo(correo);
        u.setTelefono(normalizarTelefono(req.telefono()));
        u.setRol(rol);
        u.setContrasenaHash(encoder.encode(req.contrasenaTemporal()));
        u.setEstado(Boolean.FALSE.equals(req.activo()) ? "inactivo" : "activo");
        guardar(u);
        em.refresh(u); // fecha_creacion la asigna la BD
        return UsuarioDTO.from(u);
    }

    /**
     * @param idActual id de quien hace el cambio (del token), para impedir que un administrador
     *                 se desactive o se quite el rol a sí mismo.
     */
    @Transactional
    public UsuarioDTO actualizar(Integer id, UsuarioActualizarRequestDTO req, Integer idActual) {
        Usuario u = buscar(id);
        String correo = normalizarCorreo(req.correo());
        if (usuarios.existsByCorreoIgnoreCaseAndIdUsuarioNot(correo, id)) {
            throw new ApiException(HttpStatus.CONFLICT, CORREO_DUPLICADO);
        }
        Rol rolActual = u.getRol();
        Rol nuevoRol = rolActual.getIdRol().equals(req.idRol()) ? rolActual : rolActivo(req.idRol());

        // Una cuenta pendiente sigue pendiente salvo que se marque activa (se aprueba).
        String nuevoEstado = Boolean.TRUE.equals(req.activo())
                ? "activo"
                : ("pendiente".equals(u.getEstado()) ? "pendiente" : "inactivo");

        if (id.equals(idActual)) {
            if (!"activo".equals(nuevoEstado)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "No puede desactivar su propia cuenta");
            }
            if (!rolActual.getIdRol().equals(nuevoRol.getIdRol())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "No puede cambiar su propio rol");
            }
        }

        boolean eraAdminActivo = "activo".equals(u.getEstado()) && esAdmin(rolActual);
        boolean seguiraAdminActivo = "activo".equals(nuevoEstado) && esAdmin(nuevoRol);
        if (eraAdminActivo && !seguiraAdminActivo && usuarios.contarActivosConRolExcepto(rolAdmin, id) == 0) {
            throw new ApiException(HttpStatus.CONFLICT, "Debe quedar al menos un administrador activo");
        }

        u.setNombreCompleto(req.nombreCompleto().trim());
        u.setCorreo(correo);
        u.setTelefono(normalizarTelefono(req.telefono()));
        u.setRol(nuevoRol);
        u.setEstado(nuevoEstado);
        guardar(u);
        return UsuarioDTO.from(u);
    }

    /**
     * Aprueba una cuenta creada por el registro público.
     */
    @Transactional
    public UsuarioDTO aprobar(Integer id) {
        Usuario u = buscar(id);
        if (!"pendiente".equals(u.getEstado())) {
            throw new ApiException(HttpStatus.CONFLICT, "La cuenta no está pendiente de aprobación");
        }
        if (!u.getRol().isActivo()) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "El rol solicitado está inactivo; asigne otro rol antes de aprobar la cuenta");
        }
        u.setEstado("activo");
        guardar(u);
        return UsuarioDTO.from(u);
    }

    @Transactional
    public MensajeDTO restablecerContrasena(Integer id, RestablecerContrasenaRequestDTO req) {
        Usuario u = buscar(id);
        validarContrasena(req.contrasenaTemporal());
        u.setContrasenaHash(encoder.encode(req.contrasenaTemporal()));
        intentos.limpiar(id); // levanta un posible bloqueo por intentos fallidos
        guardar(u);
        return new MensajeDTO("Contraseña restablecida correctamente");
    }

    /* ---------------- auxiliares ---------------- */

    private Usuario buscar(Integer id) {
        return usuarios.findConRolById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private Rol rolActivo(Integer idRol) {
        return roles.findById(idRol)
                .filter(Rol::isActivo)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "El rol seleccionado no es válido"));
    }

    private boolean esAdmin(Rol rol) {
        return rol.getNombreRol().equalsIgnoreCase(rolAdmin);
    }

    private void guardar(Usuario u) {
        try {
            usuarios.saveAndFlush(u);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(HttpStatus.CONFLICT, CORREO_DUPLICADO);
        }
    }

    private static void validarContrasena(String contrasena) {
        String error = PasswordPolicy.validar(contrasena);
        if (error != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, error);
        }
    }

    private static String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizarTelefono(String telefono) {
        String t = telefono == null ? "" : telefono.trim();
        return t.isEmpty() ? null : t;
    }
}