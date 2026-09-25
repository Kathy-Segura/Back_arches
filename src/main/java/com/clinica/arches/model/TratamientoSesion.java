package com.clinica.arches.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tratamiento_sesiones", schema = "clinica")
@Data
public class TratamientoSesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesion")
    private Integer idSesion;

    @Column(name = "id_tratamiento", nullable = false)
    private Integer idTratamiento;

    /** Nullable: no toda sesión viene de una cita agendada. */
    @Column(name = "id_cita")
    private Integer idCita;

    @Column(name = "fecha_sesion", nullable = false)
    private LocalDateTime fechaSesion;

    @Column(name = "descripcion_avance", length = 300)
    private String descripcionAvance;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    void alCrear() {
        LocalDateTime ahora = LocalDateTime.now();
        if (fechaSesion == null) {
            fechaSesion = ahora;
        }
        if (fechaCreacion == null) {
            fechaCreacion = ahora;
        }
    }
}

