package com.clinica.arches.config;

import com.clinica.arches.service.PermisoService;
import com.clinica.arches.service.PermisoService.Accion;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Regla central de acceso para /api/**: según el prefijo de la ruta se exige un permiso del rol en un módulo.
 * GET -> ver, POST -> crear, PUT/PATCH -> editar, DELETE -> eliminar.
 * Para cambiar qué módulo protege cada ruta, edita REGLAS (los nombres son los de modulos_sistema).
 */
@Component
public class PermisoAccessPolicy {

    private static final Logger log = LoggerFactory.getLogger(PermisoAccessPolicy.class);

    /**
     * @param modulo       null = cualquier usuario autenticado
     * @param lecturaLibre true = los GET los puede hacer cualquier usuario autenticado (catálogos); el resto exige el módulo
     */
    private record Regla(String prefijo, String modulo, boolean lecturaLibre) {
    }

    private static final List<Regla> REGLAS = List.of(
            new Regla("/api/dashboard", null, false),
            new Regla("/api/pacientes", "Pacientes", false),
            new Regla("/api/citas", "Agenda y Citas", false),
            new Regla("/api/expedientes", "Expediente Clínico", false),
            new Regla("/api/procedimientos", "Procedimientos", false),
            new Regla("/api/tratamientos", "Procedimientos", false), // no hay módulo propio: va con Procedimientos
            new Regla("/api/reportes", "Reportes", false),
            new Regla("/api/configuracion", "Configuración", false),
            new Regla("/api/personal", "Configuración", true)        // el listado alimenta combos (p. ej. odontólogos en citas)
    );

    private static final Set<String> SIN_REGLA_AVISADAS = ConcurrentHashMap.newKeySet();

    private final PermisoService permisos;

    public PermisoAccessPolicy(PermisoService permisos) {
        this.permisos = permisos;
    }

    public AuthorizationDecision decidir(Authentication auth, HttpServletRequest req) {
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return new AuthorizationDecision(false);
        }
        if (!(auth.getDetails() instanceof SesionInfo info)) {
            return new AuthorizationDecision(false);
        }
        if (info.debeCambiarContrasena()) {
            return new AuthorizationDecision(false); // hasta que cambie la contraseña temporal solo puede usar /api/auth
        }
        if (info.admin()) {
            return new AuthorizationDecision(true);
        }

        String ruta = req.getRequestURI();
        Regla regla = buscar(ruta);
        if (regla == null) {
            if (SIN_REGLA_AVISADAS.add(ruta.split("/").length > 2 ? "/" + ruta.split("/")[1] + "/" + ruta.split("/")[2] : ruta)) {
                log.warn("Ruta sin regla de permisos (acceso para cualquier usuario autenticado): {}", ruta);
            }
            return new AuthorizationDecision(true);
        }
        if (regla.modulo() == null) {
            return new AuthorizationDecision(true);
        }
        Accion accion = accionDe(req.getMethod());
        if (regla.lecturaLibre() && accion == Accion.VER) {
            return new AuthorizationDecision(true);
        }
        return new AuthorizationDecision(permisos.puede(info.idRol(), regla.modulo(), accion));
    }

    private static Regla buscar(String ruta) {
        for (Regla r : REGLAS) {
            if (ruta.equals(r.prefijo()) || ruta.startsWith(r.prefijo() + "/")) {
                return r;
            }
        }
        return null;
    }

    private static Accion accionDe(String metodo) {
        return switch (metodo) {
            case "POST" -> Accion.CREAR;
            case "PUT", "PATCH" -> Accion.EDITAR;
            case "DELETE" -> Accion.ELIMINAR;
            default -> Accion.VER; // GET, HEAD
        };
    }
}
