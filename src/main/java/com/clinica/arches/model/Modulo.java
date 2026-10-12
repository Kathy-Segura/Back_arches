package com.clinica.arches.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

/**
 * Tabla clinica.modulos_sistema (solo lectura desde este módulo).
 * Los módulos van ligados a las pantallas del sistema, por eso no se
 * administran desde la API de roles.
 */
@Entity
@Immutable
@Table(name = "modulos_sistema", schema = "clinica")
public class Modulo {

    @Id
    @Column(name = "id_modulo")
    private Integer idModulo;

    @Column(name = "nombre_modulo", nullable = false)
    private String nombreModulo;

    protected Modulo() {
        // requerido por JPA
    }

    public Integer getIdModulo() { return idModulo; }
    public String getNombreModulo() { return nombreModulo; }
}
