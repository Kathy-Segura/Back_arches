package com.clinica.arches.service;

import com.clinica.arches.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bloqueo temporal tras 5 contraseñas incorrectas. En memoria: se reinicia con el servidor y
 * no se comparte entre instancias (suficiente para una sola instancia en Render).
 */
@Service
public class LoginAttemptService {

    private static final int MAX_FALLOS = 5;
    private static final Duration BLOQUEO = Duration.ofMinutes(15);

    private record Estado(int fallos, Instant ultimo) {
    }

    private final ConcurrentHashMap<Integer, Estado> intentos = new ConcurrentHashMap<>();

    public void verificar(Integer idUsuario) {
        Estado e = intentos.get(idUsuario);
        if (e != null && e.fallos() >= MAX_FALLOS) {
            if (Instant.now().isBefore(e.ultimo().plus(BLOQUEO))) {
                throw new ApiException(HttpStatus.TOO_MANY_REQUESTS,
                        "Demasiados intentos fallidos. Intente de nuevo en 15 minutos");
            }
            intentos.remove(idUsuario);
        }
    }

    public void registrarFallo(Integer idUsuario) {
        intentos.merge(idUsuario, new Estado(1, Instant.now()),
                (viejo, nuevo) -> new Estado(viejo.fallos() + 1, nuevo.ultimo()));
    }

    public void limpiar(Integer idUsuario) {
        intentos.remove(idUsuario);
    }
}
