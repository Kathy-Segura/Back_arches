package com.clinica.arches.exception;

/** Se lanza cuando un recurso (cita, odontólogo, procedimiento, etc.) no existe. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
