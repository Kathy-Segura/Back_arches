package com.clinica.arches.model;

import jakarta.persistence.*;
        import lombok.Data;

import java.math.BigDecimal;

/**
 * Tabla clinica.catalogo_procedimientos. No existía en el esquema que
 * compartiste; se agrega en sql/V_catalogo_procedimientos.sql. Ajusta las
 * columnas ahí si tu catálogo real necesita más campos (categoría, código
 * interno, etc.).
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

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    private BigDecimal precio;

    @Column(nullable = false)
    private String estado; // 'activo' | 'inactivo'
}
