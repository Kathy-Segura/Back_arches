package com.clinica.arches.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** Tabla clinica.rol_permiso */
@Entity
@Table(name = "rol_permiso", schema = "clinica")
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

    public RolPermiso(RolPermisoId id) {
        this.id = id;
    }

    public void asignar(boolean ver, boolean crear, boolean editar, boolean eliminar) {
        this.puedeVer = ver;
        this.puedeCrear = crear;
        this.puedeEditar = editar;
        this.puedeEliminar = eliminar;
    }

    public RolPermisoId getId() { return id; }
    public boolean isPuedeVer() { return puedeVer; }
    public boolean isPuedeCrear() { return puedeCrear; }
    public boolean isPuedeEditar() { return puedeEditar; }
    public boolean isPuedeEliminar() { return puedeEliminar; }
}
