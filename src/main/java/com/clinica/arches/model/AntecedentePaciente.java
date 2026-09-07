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
    @Column(name = "id_antecedente")
    private Integer idAntecedente;

    @Column(name = "id_paciente", nullable = false)
    private Integer idPaciente;

    @Column(name = "tipo_antecedente", nullable = false)
    private String tipoAntecedente; // ej: "Familiar", "Personal", "Alérgico", "Quirúrgico"

    @Column(nullable = false)
    private String descripcion;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
}
