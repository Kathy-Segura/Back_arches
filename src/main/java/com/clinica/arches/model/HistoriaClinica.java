package com.clinica.arches.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/** Tabla clinica.historia_clinica (una por paciente). */
@Entity
@Table(name = "historia_clinica", schema = "clinica")
@Data
public class HistoriaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historia")
    private Integer idHistoria;

    @Column(name = "id_paciente", nullable = false)
    private Integer idPaciente;

    @Column(name = "antecedentes_medicos", columnDefinition = "text")
    private String antecedentesMedicos;

    @Column(name = "antecedentes_odontologicos", columnDefinition = "text")
    private String antecedentesOdontologicos;

    @Column(name = "habitos", columnDefinition = "text")
    private String habitos;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}
