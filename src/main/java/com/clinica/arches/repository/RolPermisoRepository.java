package com.clinica.arches.repository;

import com.clinica.arches.model.RolPermiso;
import com.clinica.arches.model.RolPermisoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RolPermisoRepository extends JpaRepository<RolPermiso, RolPermisoId> {

    List<RolPermiso> findByIdIdRol(Integer idRol);

    /** Permiso de un rol sobre un módulo identificado por su nombre (una sola consulta por petición). */
    @Query("select p from RolPermiso p where p.id.idRol = :idRol and p.id.idModulo = "
            + "(select m.idModulo from ModuloSistema m where m.nombreModulo = :modulo)")
    Optional<RolPermiso> findByRolYModulo(@Param("idRol") Integer idRol, @Param("modulo") String modulo);
}
