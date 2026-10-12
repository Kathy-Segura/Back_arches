package com.clinica.arches.service;

import com.clinica.arches.dto.ModuloResponse;
import com.clinica.arches.dto.PermisoModuloResponse;
import com.clinica.arches.dto.PermisoRequest;
import com.clinica.arches.dto.PermisosRequest;
import com.clinica.arches.model.Modulo;
import com.clinica.arches.model.RolPermiso;
import com.clinica.arches.model.RolPermisoId;
import com.clinica.arches.exception.ApiException;
import com.clinica.arches.repository.ModuloRepository;
import com.clinica.arches.repository.RolPermisoRepository;
import com.clinica.arches.repository.RolRepository;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermisoService {

    private final RolRepository roles;
    private final ModuloRepository modulos;
    private final RolPermisoRepository permisos;

    public PermisoService(RolRepository roles, ModuloRepository modulos, RolPermisoRepository permisos) {
        this.roles = roles;
        this.modulos = modulos;
        this.permisos = permisos;
    }

    @Transactional(readOnly = true)
    public List<ModuloResponse> listarModulos() {
        return modulos.findAllByOrderByIdModuloAsc().stream().map(ModuloResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PermisoModuloResponse> obtenerPorRol(Integer idRol) {
        asegurarRol(idRol);
        return construirMatriz(idRol);
    }

    /**
     * Reemplaza los permisos del rol para los módulos recibidos. Todo ocurre en
     * una transacción: si algo falla no se guarda nada.
     */
    @Transactional
    public List<PermisoModuloResponse> guardarPorRol(Integer idRol, PermisosRequest request) {
        asegurarRol(idRol);

        Set<Integer> modulosValidos = modulos.findAll().stream()
                .map(Modulo::getIdModulo)
                .collect(Collectors.toSet());

        // Si el mismo módulo viene repetido, prevalece el último.
        Map<Integer, PermisoRequest> recibidos = new LinkedHashMap<>();
        for (PermisoRequest p : request.permisos()) {
            if (!modulosValidos.contains(p.idModulo())) {
                throw ApiException.badRequest("El módulo " + p.idModulo() + " no existe");
            }
            recibidos.put(p.idModulo(), p);
        }

        Map<Integer, RolPermiso> existentes = permisos.findByIdIdRol(idRol).stream()
                .collect(Collectors.toMap(rp -> rp.getId().getIdModulo(), rp -> rp));

        List<RolPermiso> aGuardar = recibidos.values().stream().map(p -> {
            RolPermiso entidad = existentes.getOrDefault(
                    p.idModulo(), new RolPermiso(new RolPermisoId(idRol, p.idModulo())));

            boolean crear = p.puedeCrear();
            boolean editar = p.puedeEditar();
            boolean eliminar = p.puedeEliminar();
            // Coherencia: cualquier acción de escritura implica "ver".
            boolean ver = p.puedeVer() || crear || editar || eliminar;

            entidad.asignar(ver, crear, editar, eliminar);
            return entidad;
        }).toList();

        permisos.saveAll(aGuardar);
        return construirMatriz(idRol);
    }

    /** Todos los módulos del sistema con los permisos del rol (false si no hay fila). */
    private List<PermisoModuloResponse> construirMatriz(Integer idRol) {
        Map<Integer, RolPermiso> porModulo = new HashMap<>();
        permisos.findByIdIdRol(idRol).forEach(rp -> porModulo.put(rp.getId().getIdModulo(), rp));

        return modulos.findAllByOrderByIdModuloAsc().stream().map(m -> {
            RolPermiso rp = porModulo.get(m.getIdModulo());
            return rp == null
                    ? new PermisoModuloResponse(m.getIdModulo(), m.getNombreModulo(), false, false, false, false)
                    : new PermisoModuloResponse(
                    m.getIdModulo(), m.getNombreModulo(),
                    rp.isPuedeVer(), rp.isPuedeCrear(), rp.isPuedeEditar(), rp.isPuedeEliminar());
        }).toList();
    }

    private void asegurarRol(Integer idRol) {
        if (!roles.existsById(idRol)) {
            throw ApiException.notFound("El rol no existe");
        }
    }
}
