package com.clinica.arches.config;

import com.clinica.arches.repository.UsuarioRepository;
import com.clinica.arches.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

/**
 * Valida el Bearer token y consulta la BD en cada petición, así desactivar un usuario, cambiarle el rol
 * o marcar su contraseña como temporal surte efecto de inmediato. NO es un @Component a propósito
 * (se instancia en SecurityConfig) para que Spring Boot no lo registre dos veces.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final UsuarioRepository usuarios;
    private final String rolAdmin;

    public JwtAuthFilter(JwtService jwt, UsuarioRepository usuarios, String rolAdmin) {
        this.jwt = jwt;
        this.usuarios = usuarios;
        this.rolAdmin = rolAdmin;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims c = jwt.leerSesion(header.substring(7));
                usuarios.findConRolById(Integer.valueOf(c.getSubject()))
                        .filter(u -> "activo".equals(u.getEstado()) && u.getRol().isActivo())
                        .ifPresent(u -> {
                            var auth = new UsernamePasswordAuthenticationToken(
                                    u.getIdUsuario(), null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + normalizar(u.getRol().getNombreRol()))));
                            auth.setDetails(new SesionInfo(
                                    u.getRol().getIdRol(),
                                    u.getRol().getNombreRol().equalsIgnoreCase(rolAdmin),
                                    u.isDebeCambiarContrasena()));
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        });
            } catch (JwtException | IllegalArgumentException ignorado) {
                // Token inválido o vencido: sigue sin autenticar y la cadena responde 401 si la ruta lo exige.
            }
        }
        chain.doFilter(req, res);
    }

    /** "Odontólogo Jefe" -> "ODONTOLOGO_JEFE" */
    static String normalizar(String nombreRol) {
        return Normalizer.normalize(nombreRol, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9]+", "_")
                .toUpperCase(Locale.ROOT);
    }
}
