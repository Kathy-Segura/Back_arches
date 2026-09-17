package com.clinica.arches.model;

import jakarta.persistence.*;
        import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "citas", schema = "clinica")
@Data
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCita;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_personal", nullable = false)
    private Personal personal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_procedimiento")
    private CatalogoProcedimiento procedimiento;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos = 30;

    @Column(name = "estado_cita", nullable = false)
    private String estadoCita = "programada"; // programada | confirmada | cancelada | atendida

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_motivo")
    private CatalogoMotivoCancelacion motivo;

    private String notas;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}
