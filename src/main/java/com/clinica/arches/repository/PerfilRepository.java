package com.clinica.arches.repository;

import com.clinica.arches.dto.ActividadDTO;
import com.clinica.arches.dto.PerfilDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/** Consultas del perfil sobre usuarios, personal, roles, catalogo_especialidades y bitacora_actividad. */
@Repository
public class PerfilRepository {

    private final JdbcTemplate jdbc;

    public PerfilRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Integer> buscarIdUsuarioPorCorreo(String correo) {
        return jdbc.query(
                "SELECT id_usuario FROM clinica.usuarios WHERE LOWER(correo) = LOWER(?)",
                (rs, i) -> rs.getInt(1), correo).stream().findFirst();
    }

    public Optional<PerfilDTO> buscarPerfil(Integer idUsuario) {
        String sql = """
                SELECT u.id_usuario, u.nombre_completo, u.correo, u.telefono, u.fecha_creacion,
                       r.nombre_rol,
                       p.cargo, p.numero_licencia, p.fecha_ingreso,
                       e.nombre_especialidad,
                       COALESCE(pu.notificaciones_correo, true) AS notificaciones_correo,
                       pu.foto
                FROM clinica.usuarios u
                JOIN clinica.roles r ON r.id_rol = u.id_rol
                LEFT JOIN clinica.personal p ON p.id_usuario = u.id_usuario
                LEFT JOIN clinica.catalogo_especialidades e ON e.id_especialidad = p.id_especialidad
                LEFT JOIN clinica.preferencias_usuario pu ON pu.id_usuario = u.id_usuario
                WHERE u.id_usuario = ?
                """;
        return jdbc.query(sql, (rs, i) -> {
            Date ingreso = rs.getDate("fecha_ingreso");
            Timestamp creacion = rs.getTimestamp("fecha_creacion");
            return new PerfilDTO(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre_completo"),
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getString("nombre_rol"),
                    rs.getString("cargo"),
                    rs.getString("nombre_especialidad"),
                    rs.getString("numero_licencia"),
                    ingreso == null ? null : ingreso.toLocalDate(),
                    creacion == null ? null : creacion.toLocalDateTime(),
                    rs.getBoolean("notificaciones_correo"),
                    rs.getString("foto"));
        }, idUsuario).stream().findFirst();
    }

    public boolean correoEnUsoPorOtro(String correo, Integer idUsuario) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM clinica.usuarios WHERE LOWER(correo) = LOWER(?) AND id_usuario <> ?",
                Integer.class, correo, idUsuario);
        return n != null && n > 0;
    }

    public void actualizarUsuario(Integer idUsuario, String nombre, String correo, String telefono) {
        jdbc.update("""
                UPDATE clinica.usuarios
                SET nombre_completo = ?, correo = ?, telefono = ?
                WHERE id_usuario = ?
                """, nombre, correo, telefono, idUsuario);
    }

    /** Mantiene sincronizado el registro de personal vinculado (si existe). */
    public void actualizarPersonalVinculado(Integer idUsuario, String nombre, String correo, String telefono) {
        jdbc.update("""
                UPDATE clinica.personal
                SET nombre_completo = ?, correo = ?, telefono = ?
                WHERE id_usuario = ?
                """, nombre, correo, telefono, idUsuario);
    }

    public Optional<String> buscarHash(Integer idUsuario) {
        return jdbc.query(
                "SELECT contrasena_hash FROM clinica.usuarios WHERE id_usuario = ?",
                (rs, i) -> rs.getString(1), idUsuario).stream().findFirst();
    }

    public void actualizarContrasena(Integer idUsuario, String nuevoHash) {
        jdbc.update("""
                UPDATE clinica.usuarios
                SET contrasena_hash = ?, debe_cambiar_contrasena = false
                WHERE id_usuario = ?
                """, nuevoHash, idUsuario);
    }

    public List<ActividadDTO> listarActividad(Integer idUsuario, int limite) {
        return jdbc.query("""
                SELECT b.id_bitacora, b.accion, m.nombre_modulo, b.fecha_hora, b.direccion_ip
                FROM clinica.bitacora_actividad b
                JOIN clinica.modulos_sistema m ON m.id_modulo = b.id_modulo
                WHERE b.id_usuario = ?
                ORDER BY b.fecha_hora DESC, b.id_bitacora DESC
                LIMIT ?
                """, (rs, i) -> new ActividadDTO(
                rs.getInt("id_bitacora"),
                rs.getString("accion"),
                rs.getString("nombre_modulo"),
                rs.getTimestamp("fecha_hora").toLocalDateTime(),
                rs.getString("direccion_ip")), idUsuario, limite);
    }
}
