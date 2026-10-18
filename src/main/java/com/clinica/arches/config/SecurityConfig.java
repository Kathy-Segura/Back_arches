package com.clinica.arches.config;

import com.clinica.arches.repository.UsuarioRepository;
import com.clinica.arches.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * true  -> toda la API exige token (excepto /api/auth/* públicos). Valor para producción.
     * false -> solo para desarrollo, mientras el front aún no envía el header Authorization.
     */
    @Value("${app.security.protect-api:true}")
    private boolean protegerApi;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Evita que Spring Boot cree el usuario en memoria con contraseña generada. La autenticación es por JWT. */
    @Bean
    public UserDetailsService sinUsuariosEnMemoria() {
        return username -> {
            throw new UsernameNotFoundException(username);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwt, UsuarioRepository usuarios) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // API sin cookies de sesión: el token va en el header
                .cors(Customizer.withDefaults())       // usa la configuración de CorsConfig (WebMvcConfigurer)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> {
                    a.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    a.requestMatchers("/api/auth/usuarios", "/api/auth/roles", "/api/auth/login",
                            "/api/auth/registro", "/api/auth/recuperar", "/api/auth/restablecer").permitAll();
                    a.requestMatchers("/docs/**", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll();
                    if (protegerApi) {
                        a.anyRequest().authenticated();
                    } else {
                        a.anyRequest().permitAll();
                    }
                })
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> escribir(res, HttpStatus.UNAUTHORIZED, "Debe iniciar sesión"))
                        .accessDeniedHandler((req, res, ex) -> escribir(res, HttpStatus.FORBIDDEN, "No tiene permiso para esta acción")))
                .addFilterBefore(new JwtAuthFilter(jwt, usuarios), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private static void escribir(HttpServletResponse res, HttpStatus status, String mensaje) throws IOException {
        res.setStatus(status.value());
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"message\":\"" + mensaje + "\"}");
    }
}
