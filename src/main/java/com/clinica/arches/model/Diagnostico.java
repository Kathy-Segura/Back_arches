package com.clinica.arches.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/** Tabla clinica.diagnosticos. */
@Entity
@Table(name = "diagnosticos", schema = "clinica")
@Data
public class Diagnostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diagnostico")
    private Integer idDiagnostico;

    @Column(name = "id_paciente", nullable = false)
    private Integer idPaciente;

    @Column(name = "id_personal", nullable = false)
    private Integer idPersonal;

    @Column(name = "diagnostico", nullable = false, length = 150)
    private String diagnostico;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Column(name = "fecha_diagnostico", nullable = false)
    private LocalDate fechaDiagnostico;
}
