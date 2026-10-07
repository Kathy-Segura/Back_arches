package com.clinica.arches.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/** Tabla clinica.evolucion_clinica (notas de evolución del paciente). */
@Entity
@Table(name = "evolucion_clinica", schema = "clinica")
@Data
public class EvolucionClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evolucion")
    private Integer idEvolucion;

    @Column(name = "id_paciente", nullable = false)
    private Integer idPaciente;

    @Column(name = "id_personal", nullable = false)
    private Integer idPersonal;

    @Column(name = "nota", nullable = false, columnDefinition = "text")
    private String nota;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
}
