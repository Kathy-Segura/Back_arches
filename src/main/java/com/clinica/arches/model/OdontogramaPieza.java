package com.clinica.arches.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Tabla clinica.odontograma_pieza: una fila por (paciente, pieza FDI).
 * id_hallazgo_tipo_actual lo mantiene el trigger trg_historialhallazgo_actualiza_pieza.
 */
@Entity
@Table(name = "odontograma_pieza", schema = "clinica")
@Data
public class OdontogramaPieza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pieza")
    private Integer idPieza;

    @Column(name = "id_paciente", nullable = false)
    private Integer idPaciente;

    /** smallint en BD => Short (con Integer la validación del esquema falla). */
    @Column(name = "numero_pieza", nullable = false)
    private Short numeroPieza;

    @Column(name = "id_hallazgo_tipo_actual")
    private Integer idHallazgoTipoActual;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}
