package com.clinica.arches.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Hidden;

import java.net.URI;
import java.util.Map;

@RestController
@Hidden // evita que aparezca dentro de Swagger
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Void> root() {
        return ResponseEntity.status(302)
                .location(URI.create("/swagger-ui/index.html#/"))
                .build();
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
