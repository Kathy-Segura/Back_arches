package com.clinica.arches.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO de entrada (POST/PUT) para el CRUD de Personal.
 *
 * "estado" es opcional: en creación se fuerza a 'activo' en el service
 * sin importar lo que venga aquí; en edición sí se respeta, para poder
 * activar/desactivar personal desde el formulario.
 */
@Data
public class PersonalRequestDTO {

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @NotBlank(message = "El cargo es obligatorio")
    private String cargo; // 'Odontólogo' | 'Administrativo'

    private String telefono;
    private String correo;
    private String horarioTexto;
    private String numeroLicencia;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate fechaIngreso;

    private Integer idEspecialidad;
    private Integer idUsuario;

    /** Solo se usa en edición; en creación el service la fuerza a 'activo'. */
    private String estado;
}
