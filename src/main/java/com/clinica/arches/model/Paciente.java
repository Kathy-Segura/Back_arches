package com.clinica.arches.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pacientes")
@Data
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPaciente;

    private String nombreCompleto;

    private String cedula;

    private LocalDate fechaNacimiento;

    private String sexo;
    private String direccion;
    private String ocupacion;
    private String telefono;
    private String correo;

    private String estadoExpediente;

    private LocalDateTime fechaRegistro;
}

