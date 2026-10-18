package com.clinica.arches.dto;

import java.time.Instant;

public record LoginResponseDTO(String token, Instant expiraEn, UsuarioSesionDTO usuario) {
}
