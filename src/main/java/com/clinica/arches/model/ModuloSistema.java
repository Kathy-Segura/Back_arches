package com.clinica.arches.model;

import jakarta.persistence.*;

/** Tabla clinica.modulos_sistema (catálogo de solo lectura). */
@Entity
@Table(name = "modulos_sistema")
public class ModuloSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modulo")
    private Integer idModulo;

    @Column(name = "nombre_modulo", nullable = false, length = 50)
    private String nombreModulo;

    public Integer getIdModulo() { return idModulo; }
    public String getNombreModulo() { return nombreModulo; }
}
