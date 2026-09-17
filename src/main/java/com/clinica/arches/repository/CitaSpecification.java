package com.clinica.arches.repository;

import com.clinica.arches.model.Cita;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Sustenta el filtrado dinámico de la pestaña "Listado" (y sus exportaciones).
 * A diferencia de las queries JPQL con parámetros opcionales
 * ("(:param IS NULL OR columna = :param)"), aquí cada condición solo se
 * agrega al WHERE si el filtro realmente viene informado. Ningún parámetro
 * null llega a la SQL final, así que no se dispara ninguno de los dos bugs
 * de inferencia de tipo de PostgreSQL/pgjdbc (bytea en CAST, o "could not
 * determine data type") que sí aparecen con el patrón JPQL estático.
 */
public class CitaSpecification {

    private CitaSpecification() {
    }

    public static Specification<Cita> conFiltros(String search,
                                                 Integer idPersonal,
                                                 String estado,
                                                 LocalDateTime desde,
                                                 LocalDateTime hasta) {
        return (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                Join<Object, Object> paciente = root.join("paciente", JoinType.LEFT);
                predicados.add(cb.like(
                        cb.lower(paciente.get("nombreCompleto")),
                        "%" + search.trim().toLowerCase() + "%"));
            }

            if (idPersonal != null) {
                predicados.add(cb.equal(root.get("personal").get("idPersonal"), idPersonal));
            }

            if (estado != null && !estado.isBlank()) {
                predicados.add(cb.equal(root.get("estadoCita"), estado));
            }

            if (desde != null) {
                predicados.add(cb.greaterThanOrEqualTo(root.get("fechaHora"), desde));
            }

            if (hasta != null) {
                predicados.add(cb.lessThanOrEqualTo(root.get("fechaHora"), hasta));
            }

            return cb.and(predicados.toArray(new Predicate[0]));
        };
    }
}
