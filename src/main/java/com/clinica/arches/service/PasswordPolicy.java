package com.clinica.arches.service;

import java.nio.charset.StandardCharsets;

/** Misma política que src/lib/password.ts: 8+ caracteres, con letras y números (BCrypt admite máx. 72 bytes). */
public final class PasswordPolicy {

    private PasswordPolicy() {
    }

    /** @return mensaje de error, o null si la contraseña es válida. */
    public static String validar(String contrasena) {
        if (contrasena == null || contrasena.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres";
        }
        if (contrasena.getBytes(StandardCharsets.UTF_8).length > 72) {
            return "La contraseña es demasiado larga (máximo 72 bytes)";
        }
        boolean letra = contrasena.codePoints().anyMatch(Character::isLetter);
        boolean numero = contrasena.codePoints().anyMatch(Character::isDigit);
        if (!letra || !numero) {
            return "La contraseña debe combinar letras y números";
        }
        return null;
    }
}
