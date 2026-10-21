package com.clinica.arches.dto;

import java.time.LocalDateTime;

public record ActividadDTO(
        Integer idBitacora,
        String accion,
        String modulo,
        LocalDateTime fechaHora,
        String direccionIp
) {}
