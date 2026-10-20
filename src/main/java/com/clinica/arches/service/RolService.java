package com.clinica.arches.service;

import com.clinica.arches.dto.EstadoRolRequestDTO;
import com.clinica.arches.dto.RolDTO;
import com.clinica.arches.dto.RolRequestDTO;
import com.clinica.arches.exception.ApiException;
import com.clinica.arches.model.Rol;
import com.clinica.arches.repository.RolRepository;
import com.clinica.arches.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** CRUD de roles. Baja lógica (activo = false), nunca se elimina un rol. */
@Service
public class RolService {

    private static final String NOMBRE_DUPLICADO = "Ya existe un rol con ese nombre";

    private final RolRepository roles;
    private final UsuarioRepository usuarios;
    private final String rolAdmin;

    public RolService(RolRepository roles, UsuarioRepository usuarios,
                      @Value("${app.security.admin-role:Administrador}") String rolAdmin) {
        this.roles = roles;
        this.usuarios = usuarios;
        this.rolAdmin = rolAdmin;
    }

    @Transactional(readOnly = true)
    public List<RolDTO> listar(boolean incluirInactivos) {
        List<Rol> lista = incluirInactivos ? roles.findAllByOrderByIdRolAsc() : roles.findByActivoTrueOrderByIdRolAsc();
        return lista.stream().map(RolDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public RolDTO obtener(Integer id) {
        return RolDTO.from(buscar(id));
    }

    @Transactional
    public RolDTO crear(RolRequestDTO req) {
        String nombre = req.nombreRol().trim();
        if (roles.existsByNombreRolIgnoreCase(nombre)) {
            throw new ApiException(HttpStatus.CONFLICT, NOMBRE_DUPLICADO);
        }
        Rol rol = new Rol(nombre, descripcion(req.descripcion()));
        return RolDTO.from(guardar(rol));
    }

    @Transactional
    public RolDTO actualizar(Integer id, RolRequestDTO req) {
        Rol rol = buscar(id);
        String nombre = req.nombreRol().trim();
        if (roles.existsByNombreRolIgnoreCaseAndIdRolNot(nombre, id)) {
            throw new ApiException(HttpStatus.CONFLICT, NOMBRE_DUPLICADO);
        }
        if (esAdmin(rol) && !nombre.equalsIgnoreCase(rol.getNombreRol())) {
            throw new ApiException(HttpStatus.CONFLICT, "No se puede renombrar el rol administrador");
        }
        rol.setNombreRol(nombre);
        rol.setDescripcion(descripcion(req.descripcion()));
        return RolDTO.from(guardar(rol));
    }

    @Transactional
    public RolDTO cambiarEstado(Integer id, EstadoRolRequestDTO req) {
        Rol rol = buscar(id);
        boolean activo = Boolean.TRUE.equals(req.activo());
        if (!activo) {
            if (esAdmin(rol)) {
                throw new ApiException(HttpStatus.CONFLICT, "No se puede desactivar el rol administrador");
            }
            long enUso = usuarios.countByRolIdRolAndEstado(id, "activo");
            if (enUso > 0) {
                throw new ApiException(HttpStatus.CONFLICT,
                        "El rol tiene " + enUso + " usuario(s) activo(s); reasígnelos antes de desactivarlo");
            }
        }
        rol.setActivo(activo);
        return RolDTO.from(guardar(rol));
    }

    private Rol buscar(Integer id) {
        return roles.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
    }

    private boolean esAdmin(Rol rol) {
        return rol.getNombreRol().equalsIgnoreCase(rolAdmin);
    }

    private Rol guardar(Rol rol) {
        try {
            return roles.saveAndFlush(rol);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(HttpStatus.CONFLICT, NOMBRE_DUPLICADO);
        }
    }

    private static String descripcion(String d) {
        String t = d == null ? "" : d.trim();
        return t.isEmpty() ? null : t;
    }
}
