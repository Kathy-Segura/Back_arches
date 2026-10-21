package com.clinica.arches.dto;

import jakarta.validation.constraints.NotNull;

public record PreferenciasRequest(@NotNull Boolean notificacionesCorreo) {}
