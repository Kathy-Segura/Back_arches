package com.clinica.arches.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/** Tabla clinica.odontograma_historial_hallazgo: cada cambio de hallazgo en una pieza. */
@Entity
@Table(name = "odontograma_historial_hallazgo", schema = "clinica")
@Data
public class OdontogramaHistorialHallazgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")
    private Integer idRegistro;

    @Column(name = "id_pieza", nullable = false)
    private Integer idPieza;

    @Column(name = "id_hallazgo_tipo", nullable = false)
    private Integer idHallazgoTipo;

    @Column(name = "id_personal", nullable = false)
    private Integer idPersonal;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "observaciones", length = 300)
    private String observaciones;
}
