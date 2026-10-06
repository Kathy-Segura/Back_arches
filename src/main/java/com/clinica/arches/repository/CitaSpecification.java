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
 * Filtrado dinámico de citas (listado, exportaciones e historial por paciente).
 * Cada condición solo se agrega al WHERE si el filtro viene informado.
 */
public class CitaSpecification {

    private CitaSpecification() {
    }

    /** Firma anterior: se conserva para no romper llamadas existentes. */
    public static Specification<Cita> conFiltros(String search,
                                                 Integer idPersonal,
                                                 String estado,
                                                 LocalDateTime desde,
                                                 LocalDateTime hasta) {
        return conFiltros(search, null, idPersonal, estado, desde, hasta);
    }

    /** Nueva firma: agrega filtro exacto por id de paciente. */
    public static Specification<Cita> conFiltros(String search,
                                                 Integer idPaciente,
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

            if (idPaciente != null) {
                predicados.add(cb.equal(root.get("paciente").get("idPaciente"), idPaciente));
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
