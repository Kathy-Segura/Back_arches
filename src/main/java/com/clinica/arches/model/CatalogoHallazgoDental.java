package com.clinica.arches.model;

import jakarta.persistence.*;
import lombok.Data;

/** Tabla clinica.catalogo_hallazgos_dentales (caries, obturación, ausente, etc.). */
@Entity
@Table(name = "catalogo_hallazgos_dentales", schema = "clinica")
@Data
public class CatalogoHallazgoDental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hallazgo_tipo")
    private Integer idHallazgoTipo;

    @Column(name = "codigo_hallazgo", nullable = false, length = 20)
    private String codigoHallazgo;

    @Column(name = "nombre_hallazgo", nullable = false, length = 60)
    private String nombreHallazgo;

    @Column(name = "color_representativo", length = 20)
    private String colorRepresentativo;
}
