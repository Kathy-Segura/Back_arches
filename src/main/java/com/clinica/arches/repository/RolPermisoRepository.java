package com.clinica.arches.repository;

import com.clinica.arches.model.RolPermiso;
import com.clinica.arches.model.RolPermisoId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolPermisoRepository extends JpaRepository<RolPermiso, RolPermisoId> {

    /** Propiedad "id.idRol" de la clave compuesta. */
    List<RolPermiso> findByIdIdRol(Integer idRol);
}
