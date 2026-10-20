package com.clinica.arches.service;

import com.clinica.arches.dto.*;
import com.clinica.arches.exception.ApiException;
import com.clinica.arches.model.ModuloSistema;
import com.clinica.arches.model.Rol;
import com.clinica.arches.model.RolPermiso;
import com.clinica.arches.repository.ModuloSistemaRepository;
import com.clinica.arches.repository.RolPermisoRepository;
import com.clinica.arches.repository.RolRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Permisos por rol y módulo.
 * - El rol administrador (app.security.admin-role) tiene acceso total y no se edita: evita bloquearse a sí mismo.
 * - "Usuarios" y "Roles y Permisos" son exclusivos del administrador (nunca se conceden a otros roles).
 * - Sin permiso de ver no puede haber crear, editar ni eliminar.
 */
@Service
public class PermisoService {

    public enum Accion { VER, CREAR, EDITAR, ELIMINAR }

    public static final Set<String> MODULOS_EXCLUSIVOS_ADMIN = Set.of("Usuarios", "Roles y Permisos");

    private final ModuloSistemaRepository modulos;
    private final RolPermisoRepository permisos;
    private final RolRepository roles;
    private final String rolAdmin;

    public PermisoService(ModuloSistemaRepository modulos, RolPermisoRepository permisos, RolRepository roles,
                          @Value("${app.security.admin-role:Administrador}") String rolAdmin) {
        this.modulos = modulos;
        this.permisos = permisos;
        this.roles = roles;
        this.rolAdmin = rolAdmin;
    }

    public boolean esAdmin(Rol rol) {
        return rol.getNombreRol().equalsIgnoreCase(rolAdmin);
    }

    @Transactional(readOnly = true)
    public List<ModuloDTO> listarModulos() {
        return modulos.findAllByOrderByIdModuloAsc().stream()
                .map(m -> new ModuloDTO(m.getIdModulo(), m.getNombreModulo())).toList();
    }

    /** Matriz completa (todos los módulos; en false lo que aún no se asignó). */
    @Transactional(readOnly = true)
    public List<PermisoModuloDTO> matriz(Integer idRol) {
        Rol rol = buscarRol(idRol);
        return construirMatriz(rol);
    }

    @Transactional
    public List<PermisoModuloDTO> guardar(Integer idRol, GuardarPermisosRequestDTO req) {
        Rol rol = buscarRol(idRol);
        if (esAdmin(rol)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "El rol administrador tiene acceso total y sus permisos no se pueden modificar");
        }
        Map<Integer, ModuloSistema> catalogo = new HashMap<>();
        modulos.findAll().forEach(m -> catalogo.put(m.getIdModulo(), m));

        Map<Integer, PermisoInputDTO> porModulo = new LinkedHashMap<>();
        for (PermisoInputDTO p : req.permisos()) {
            porModulo.put(p.idModulo(), p); // si se repite un módulo, gana el último
        }

        for (PermisoInputDTO p : porModulo.values()) {
            ModuloSistema m = catalogo.get(p.idModulo());
            if (m == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Módulo no válido: " + p.idModulo());
            }
            boolean algunaAccion = p.puedeVer() || p.puedeCrear() || p.puedeEditar() || p.puedeEliminar();
            if (MODULOS_EXCLUSIVOS_ADMIN.contains(m.getNombreModulo()) && algunaAccion) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "El módulo " + m.getNombreModulo() + " es exclusivo del administrador");
            }
            if (!p.puedeVer() && (p.puedeCrear() || p.puedeEditar() || p.puedeEliminar())) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "No se puede crear, editar ni eliminar sin permiso de ver en " + m.getNombreModulo());
            }
        }

        Map<Integer, RolPermiso> existentes = new HashMap<>();
        permisos.findByIdIdRol(idRol).forEach(rp -> existentes.put(rp.getId().getIdModulo(), rp));
        for (PermisoInputDTO p : porModulo.values()) {
            RolPermiso rp = existentes.getOrDefault(p.idModulo(), new RolPermiso(idRol, p.idModulo()));
            rp.setPuedeVer(p.puedeVer());
            rp.setPuedeCrear(p.puedeCrear());
            rp.setPuedeEditar(p.puedeEditar());
            rp.setPuedeEliminar(p.puedeEliminar());
            permisos.save(rp);
        }
        permisos.flush();
        return construirMatriz(rol);
    }

    /** Permisos que recibe el front al iniciar sesión: solo los módulos en los que el rol puede hacer algo. */
    @Transactional(readOnly = true)
    public List<PermisoModuloDTO> paraSesion(Rol rol) {
        return construirMatriz(rol).stream()
                .filter(p -> esAdmin(rol)
                        || (!MODULOS_EXCLUSIVOS_ADMIN.contains(p.nombreModulo())
                        && (p.puedeVer() || p.puedeCrear() || p.puedeEditar() || p.puedeEliminar())))
                .toList();
    }

    /** Usado por la regla central de acceso del back (una consulta por petición). */
    @Transactional(readOnly = true)
    public boolean puede(Integer idRol, String modulo, Accion accion) {
        if (MODULOS_EXCLUSIVOS_ADMIN.contains(modulo)) {
            return false; // el administrador nunca llega aquí: se resuelve antes
        }
        return permisos.findByRolYModulo(idRol, modulo).map(p -> switch (accion) {
            case VER -> p.isPuedeVer();
            case CREAR -> p.isPuedeVer() && p.isPuedeCrear();
            case EDITAR -> p.isPuedeVer() && p.isPuedeEditar();
            case ELIMINAR -> p.isPuedeVer() && p.isPuedeEliminar();
        }).orElse(false);
    }

    /* ---------------- auxiliares ---------------- */

    private Rol buscarRol(Integer idRol) {
        return roles.findById(idRol)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
    }

    private List<PermisoModuloDTO> construirMatriz(Rol rol) {
        boolean admin = esAdmin(rol);
        Map<Integer, RolPermiso> porModulo = new HashMap<>();
        if (!admin) {
            permisos.findByIdIdRol(rol.getIdRol()).forEach(rp -> porModulo.put(rp.getId().getIdModulo(), rp));
        }
        return modulos.findAllByOrderByIdModuloAsc().stream().map(m -> {
            if (admin) {
                return new PermisoModuloDTO(m.getIdModulo(), m.getNombreModulo(), true, true, true, true);
            }
            RolPermiso rp = porModulo.get(m.getIdModulo());
            return rp == null
                    ? new PermisoModuloDTO(m.getIdModulo(), m.getNombreModulo(), false, false, false, false)
                    : new PermisoModuloDTO(m.getIdModulo(), m.getNombreModulo(), rp.isPuedeVer(), rp.isPuedeCrear(),
                    rp.isPuedeEditar(), rp.isPuedeEliminar());
        }).toList();
    }
}
