package com.clinica.arches.dto;

import jakarta.validation.constraints.Size;

/** foto = data URL (data:image/...;base64,...) o null para quitarla. */
public record FotoRequest(@Size(max = 400_000) String foto) {}
