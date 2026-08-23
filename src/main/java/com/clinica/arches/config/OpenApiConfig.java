package com.clinica.arches.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI archesOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("ARCHES API")
                .description("API del sistema de gestión clínica odontológica ARCHES")
                .version("v1.0"));
    }
}
