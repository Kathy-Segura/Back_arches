package com.clinica.arches.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/** Clave compuesta (id_rol, id_modulo) de clinica.rol_permiso */
@Embeddable
public class RolPermisoId implements Serializable {

    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "id_modulo")
    private Integer idModulo;

    protected RolPermisoId() {
        // requerido por JPA
    }

    public RolPermisoId(Integer idRol, Integer idModulo) {
        this.idRol = idRol;
        this.idModulo = idModulo;
    }

    public Integer getIdRol() { return idRol; }
    public Integer getIdModulo() { return idModulo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolPermisoId other)) return false;
        return Objects.equals(idRol, other.idRol) && Objects.equals(idModulo, other.idModulo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRol, idModulo);
    }
}
