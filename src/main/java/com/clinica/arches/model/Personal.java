package com.clinica.arches.model;

import jakarta.persistence.*;
        import lombok.Data;

import java.time.LocalDate;

/**
 * Tabla clinica.personal. Incluye tanto odontólogos como administrativos
 * (diferenciados por "cargo"). El módulo de citas solo usa los que tienen
 * cargo = 'Odontólogo' y estado = 'activo'.
 *
 * id_usuario / id_especialidad se dejan como Integer simple (sin @ManyToOne)
 * porque los módulos de usuarios y catálogo de especialidades todavía no
 * existen en el proyecto. Cuando existan, se pueden convertir a relaciones
 * @ManyToOne sin tocar el resto del módulo de citas.
 */
@Entity
@Table(name = "personal", schema = "clinica")
@Data
public class Personal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPersonal;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "id_especialidad")
    private Integer idEspecialidad;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    private String cargo; // 'Odontólogo' | 'Administrativo'

    private String telefono;
    private String correo;

    @Column(nullable = false)
    private String estado; // 'activo' | 'inactivo'

    @Column(name = "horario_texto")
    private String horarioTexto;

    @Column(name = "numero_licencia")
    private String numeroLicencia;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;
}
