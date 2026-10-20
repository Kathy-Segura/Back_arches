package com.clinica.arches.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** Tabla clinica.rol_permiso: qué puede hacer un rol en cada módulo. */
@Entity
@Table(name = "rol_permiso")
public class RolPermiso {

    @EmbeddedId
    private RolPermisoId id;

    @Column(name = "puede_ver", nullable = false)
    private boolean puedeVer;

    @Column(name = "puede_crear", nullable = false)
    private boolean puedeCrear;

    @Column(name = "puede_editar", nullable = false)
    private boolean puedeEditar;

    @Column(name = "puede_eliminar", nullable = false)
    private boolean puedeEliminar;

    protected RolPermiso() {
        // requerido por JPA
    }

    public RolPermiso(Integer idRol, Integer idModulo) {
        this.id = new RolPermisoId(idRol, idModulo);
    }

    public RolPermisoId getId() { return id; }
    public boolean isPuedeVer() { return puedeVer; }
    public void setPuedeVer(boolean v) { this.puedeVer = v; }
    public boolean isPuedeCrear() { return puedeCrear; }
    public void setPuedeCrear(boolean v) { this.puedeCrear = v; }
    public boolean isPuedeEditar() { return puedeEditar; }
    public void setPuedeEditar(boolean v) { this.puedeEditar = v; }
    public boolean isPuedeEliminar() { return puedeEliminar; }
    public void setPuedeEliminar(boolean v) { this.puedeEliminar = v; }
}
