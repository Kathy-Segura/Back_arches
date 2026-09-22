package com.clinica.arches.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO de respuesta para el CRUD completo del módulo de Personal
 * (listado, detalle, y resultado de crear/actualizar).
 *
 * Distinto del {@link PersonalDTO} liviano que ya existía, que se deja
 * intacto porque lo sigue usando el selector de odontólogos del módulo
 * de citas.
 *
 * "especialidad" no se incluye todavía: el módulo/catálogo de
 * especialidades no existe aún, por lo que idEspecialidad queda como
 * un Integer sin resolver a nombre (ver comentario en la entidad Personal).
 */
@Data
public class PersonalDetalleDTO {
    private Integer idPersonal;
    private Integer idUsuario;
    private Integer idEspecialidad;
    private String nombreCompleto;
    private String cargo; // 'Odontólogo' | 'Administrativo'
    private String telefono;
    private String correo;
    private String estado; // 'activo' | 'inactivo'
    private String horarioTexto;
    private String numeroLicencia;
    private LocalDate fechaIngreso;
}
