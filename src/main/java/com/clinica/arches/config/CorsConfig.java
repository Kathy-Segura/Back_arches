package com.clinica.arches.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
/**
 * Orígenes permitidos (lista separada por comas, sin "/" al final). Se pueden sobrescribir con la propiedad
 * app.cors.allowed-origins, (por ejemplo, desde la variable de entorno CORS_ORIGINS en producción).
 */
    @Value("${app.cors.allowed-origins:"
            + "http://localhost:8082,"
            + "https://sistema-clinica-arches.vercel.app}")
    private String[] allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(allowedOrigins)
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        // El navegador guarda la respuesta del preflight 1 hora y evita repetir la petición OPTIONS en cada llamada.
                        .maxAge(3600);
            }
        };
    }
}
