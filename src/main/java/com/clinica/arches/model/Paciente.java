package com.clinica.arches.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pacientes", schema = "clinica")
@Data
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPaciente;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    private String cedula;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    private String sexo;
    private String direccion;
    private String ocupacion;
    private String telefono;
    private String correo;

    @Column(name = "estado_expediente")
    private String estadoExpediente;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
}
