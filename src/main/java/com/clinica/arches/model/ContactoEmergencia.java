package com.clinica.arches.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "contactos_emergencia")
@Data
public class ContactoEmergencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contacto")
    private Integer idContacto;

    @Column(name = "id_paciente", nullable = false)
    private Integer idPaciente;

    @Column(name = "nombre_contacto", nullable = false)
    private String nombreContacto;

    @Column(nullable = false)
    private String telefono;

    private String parentesco;

}
