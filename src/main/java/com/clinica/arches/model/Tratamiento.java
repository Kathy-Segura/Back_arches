package com.clinica.arches.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tratamientos", schema = "clinica")
@Data
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tratamiento")
    private Integer idTratamiento;

    @Column(name = "id_paciente", nullable = false)
    private Integer idPaciente;

    @Column(name = "id_procedimiento", nullable = false)
    private Integer idProcedimiento;

    @Column(name = "id_personal", nullable = false)
    private Integer idPersonal;

    @Column(name = "fecha_programada")
    private LocalDate fechaProgramada;

    @Column(name = "costo_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoTotal;

    @Column(name = "sesiones_planificadas", nullable = false)
    private Integer sesionesPlanificadas = 1;

    /** 'propuesto' | 'pendiente' | 'en_proceso' | 'completado' — ver CHECK ck_tratamientos_estado_avance */
    @Column(name = "estado_avance", nullable = false, length = 15)
    private String estadoAvance = "propuesto";

    /** 'pendiente' | 'parcial' | 'pagado' — ver CHECK ck_tratamientos_estado_pago */
    @Column(name = "estado_pago", nullable = false, length = 15)
    private String estadoPago = "pendiente";

    @Column(name = "notas", length = 300)
    private String notas;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    void alCrear() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
}
