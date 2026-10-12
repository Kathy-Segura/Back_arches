package com.clinica.arches.service;

import com.clinica.arches.dto.RolRequest;
import com.clinica.arches.dto.RolResponse;
import com.clinica.arches.model.Rol;
import com.clinica.arches.exception.ApiException;
import com.clinica.arches.repository.RolRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolService {

    private final RolRepository roles;

    public RolService(RolRepository roles) {
        this.roles = roles;
    }

    @Transactional(readOnly = true)
    public List<RolResponse> listar(boolean incluirInactivos) {
        List<Rol> lista = incluirInactivos
                ? roles.findAllByOrderByIdRolAsc()
                : roles.findByActivoTrueOrderByIdRolAsc();
        return lista.stream().map(RolResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RolResponse obtener(Integer idRol) {
        return RolResponse.from(buscar(idRol));
    }

    @Transactional
    public RolResponse crear(RolRequest request) {
        String nombre = request.nombreRol().trim();
        if (roles.existsByNombreRolIgnoreCase(nombre)) {
            throw ApiException.conflict("Ya existe un rol llamado \"" + nombre + "\"");
        }
        Rol rol = new Rol(nombre, limpiar(request.descripcion()));
        return RolResponse.from(roles.save(rol));
    }

    @Transactional
    public RolResponse actualizar(Integer idRol, RolRequest request) {
        Rol rol = buscar(idRol);
        String nombre = request.nombreRol().trim();
        if (roles.existsByNombreRolIgnoreCaseAndIdRolNot(nombre, idRol)) {
            throw ApiException.conflict("Ya existe un rol llamado \"" + nombre + "\"");
        }
        rol.setNombreRol(nombre);
        rol.setDescripcion(limpiar(request.descripcion()));
        return RolResponse.from(roles.save(rol));
    }

    @Transactional
    public RolResponse cambiarEstado(Integer idRol, boolean activo) {
        Rol rol = buscar(idRol);
        rol.setActivo(activo);
        return RolResponse.from(roles.save(rol));
    }

    private Rol buscar(Integer idRol) {
        return roles.findById(idRol).orElseThrow(() -> ApiException.notFound("El rol no existe"));
    }

    private String limpiar(String texto) {
        if (texto == null) return null;
        String limpio = texto.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}
