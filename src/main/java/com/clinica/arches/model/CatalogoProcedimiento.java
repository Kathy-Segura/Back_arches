package com.clinica.arches.model;

import jakarta.persistence.*;
        import lombok.Data;

import java.math.BigDecimal;

/**
 * Tabla clinica.catalogo_procedimientos. Se agregaron `categoria` y
 * `descripcion` para sustentar el listado administrativo del módulo de
 * Procedimientos (antes solo tenía los campos mínimos para el selector de
 * citas). Ver V_catalogo_procedimientos_ampliar.sql para la migración.
 */
@Entity
@Table(name = "catalogo_procedimientos", schema = "clinica")
@Data
public class CatalogoProcedimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProcedimiento;

    @Column(name = "nombre_procedimiento", nullable = false)
    private String nombreProcedimiento;

    @Column(nullable = false)
    private String categoria;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    private BigDecimal precio;

    @Column(columnDefinition = "text")
    private String descripcion;

    @Column(nullable = false)
    private String estado; // 'activo' | 'inactivo'
}

