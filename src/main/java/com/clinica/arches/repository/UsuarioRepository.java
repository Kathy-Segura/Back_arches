package com.clinica.arches.repository;

import com.clinica.arches.dto.UsuarioLoginItemDTO;
import com.clinica.arches.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    @Query("select u from Usuario u join fetch u.rol where u.idUsuario = :id")
    Optional<Usuario> findConRolById(@Param("id") Integer id);

    /** Solo id y nombre de cuentas activas: es lo único que se expone públicamente para el combo del login. */
    @Query("select new com.clinica.arches.dto.UsuarioLoginItemDTO(u.idUsuario, u.nombreCompleto) "
            + "from Usuario u where u.estado = 'activo' order by u.nombreCompleto")
    List<UsuarioLoginItemDTO> findActivosParaLogin();
}
