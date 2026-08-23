package com.clinica.arches.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PacienteDTO {
    private Integer idPaciente;
    private String nombreCompleto;
    private String cedula;
    private LocalDate fechaNacimiento;
    private Integer edad;
    private String sexo;
    private String direccion;
    private String ocupacion;
    private String telefono;
    private String correo;
    private String estadoExpediente;
    private List<ContactoEmergenciaDTO> contactos;
    private List<AntecedenteDTO> antecedentes;
}
