package com.clinica.arches.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** cargo/especialidad/numeroLicencia/fechaIngreso son null si el usuario no está vinculado a personal. */
public record PerfilDTO(
        Integer idUsuario,
        String nombreCompleto,
        String correo,
        String telefono,
        String rol,
        String cargo,
        String especialidad,
        String numeroLicencia,
        LocalDate fechaIngreso,
        LocalDateTime fechaCreacion,
        boolean notificacionesCorreo,
        String foto
) {}
