package com.clinica.arches.service;

import com.clinica.arches.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private static final String USO_SESION = "sesion";
    private static final String USO_RESET = "reset";
    private static final Duration VIGENCIA_RESET = Duration.ofMinutes(30);

    public record Emitido(String token, Instant expiraEn) {
    }

    private final SecretKey key;
    private final Duration vigenciaNormal;
    private final Duration vigenciaRecordarme;

    /** app.jwt.secret: Base64 de al menos 32 bytes (openssl rand -base64 48). Sin valor por defecto a propósito. */
    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-minutes:480}") long minutos,
                      @Value("${app.jwt.remember-expiration-minutes:10080}") long minutosRecordarme) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.vigenciaNormal = Duration.ofMinutes(minutos);
        this.vigenciaRecordarme = Duration.ofMinutes(minutosRecordarme);
    }

    public Emitido crearSesion(Usuario u, boolean recordarme) {
        Instant ahora = Instant.now();
        Instant expira = ahora.plus(recordarme ? vigenciaRecordarme : vigenciaNormal);
        String token = Jwts.builder()
                .subject(String.valueOf(u.getIdUsuario()))
                .claim("uso", USO_SESION)
                .claim("rol", u.getRol().getNombreRol())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expira))
                .signWith(key)
                .compact();
        return new Emitido(token, expira);
    }

    /** Token de un solo uso para restablecer contraseña: queda inválido en cuanto cambia el hash guardado. */
    public String crearTokenRestablecer(Usuario u) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(u.getIdUsuario()))
                .claim("uso", USO_RESET)
                .claim("ph", huella(u))
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(VIGENCIA_RESET)))
                .signWith(key)
                .compact();
    }

    public String huella(Usuario u) {
        return Integer.toHexString(u.getContrasenaHash().hashCode());
    }

    public Claims leerSesion(String token) {
        return leer(token, USO_SESION);
    }

    public Claims leerReset(String token) {
        return leer(token, USO_RESET);
    }

    private Claims leer(String token, String usoEsperado) {
        Claims c = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        if (!usoEsperado.equals(c.get("uso", String.class))) {
            throw new JwtException("Token con uso no válido");
        }
        return c;
    }
}
