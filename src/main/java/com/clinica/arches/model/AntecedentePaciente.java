package com.clinica.arches.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "antecedentes_paciente")
@Data
public class AntecedentePaciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAntecedente;

    private Integer idPaciente;

    private String tipoAntecedente;

    private String descripcion;

    private LocalDateTime fechaRegistro;
}
