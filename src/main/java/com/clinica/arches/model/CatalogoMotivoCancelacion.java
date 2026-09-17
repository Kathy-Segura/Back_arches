package com.clinica.arches.model;

import jakarta.persistence.*;
        import lombok.Data;

@Entity
@Table(name = "catalogo_motivos_cancelacion", schema = "clinica")
@Data
public class CatalogoMotivoCancelacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMotivo;

    @Column(name = "nombre_motivo", nullable = false)
    private String nombreMotivo;
}
