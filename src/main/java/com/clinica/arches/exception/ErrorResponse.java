package com.clinica.arches.exception;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Forma del JSON de error que recibe el front. ApiError (lib/api/http.ts)
 * espera poder leer "message" y usar el status HTTP de la respuesta.
 */
@Data
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
