package com.clinica.arches.service;

import com.clinica.arches.dto.CargaOdontologoDTO;
import com.clinica.arches.dto.CitaDTO;
import com.clinica.arches.dto.PacientesNuevosVsRecurrentesDTO;
import com.clinica.arches.dto.ReporteActividadDTO;
import com.clinica.arches.dto.ReporteProcedimientoDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Módulo Reportes. Solo lectura.
 * - Citas: reutiliza CitaService.listarConFiltrosSinPaginar (misma lógica que el listado).
 * - Procedimientos y actividad: SQL nativo con filtros opcionales (solo se agrega la condición si el filtro viene).
 * - Gráficos: leen las vistas de la BD.
 */
@Service
public class ReporteService {

    private static final Set<String> ESTADOS_CITA = Set.of("programada", "confirmada", "atendida", "cancelada");
    private static final Set<String> ESTADOS_AVANCE = Set.of("propuesto", "pendiente", "en_proceso", "completado");

    @PersistenceContext
    private EntityManager entityManager;

    private final CitaService citaService;

    public ReporteService(CitaService citaService) {
        this.citaService = citaService;
    }

    // ------------------------------------------------------------------ Reportes tabulares

    @Transactional(readOnly = true)
    public List<CitaDTO> reporteCitas(LocalDate desde, LocalDate hasta, String estado) {
        validarRango(desde, hasta);
        String estadoNormalizado = normalizarEstado(estado, ESTADOS_CITA);
        return citaService.listarConFiltrosSinPaginar(null, null, estadoNormalizado, inicioDia(desde), finDia(hasta));
    }

    /** Una fila por tratamiento. El rango Desde/Hasta se aplica sobre fecha_programada. */
    @Transactional(readOnly = true)
    public List<ReporteProcedimientoDTO> reporteProcedimientos(LocalDate desde, LocalDate hasta, String estadoAvance) {
        validarRango(desde, hasta);
        String estado = normalizarEstado(estadoAvance, ESTADOS_AVANCE);

        StringBuilder sql = new StringBuilder("""
                SELECT t.id_tratamiento, pa.nombre_completo, cp.nombre_procedimiento, cp.categoria,
                       t.fecha_programada, pe.nombre_completo, t.costo_total, t.estado_avance, t.estado_pago
                FROM clinica.tratamientos t
                JOIN clinica.pacientes pa ON pa.id_paciente = t.id_paciente
                JOIN clinica.catalogo_procedimientos cp ON cp.id_procedimiento = t.id_procedimiento
                JOIN clinica.personal pe ON pe.id_personal = t.id_personal
                WHERE 1 = 1
                """);
        Map<String, Object> params = new HashMap<>();
        if (desde != null) {
            sql.append(" AND t.fecha_programada >= :desde");
            params.put("desde", desde);
        }
        if (hasta != null) {
            sql.append(" AND t.fecha_programada <= :hasta");
            params.put("hasta", hasta);
        }
        if (estado != null) {
            sql.append(" AND t.estado_avance = :estado");
            params.put("estado", estado);
        }
        sql.append(" ORDER BY t.fecha_programada DESC NULLS LAST, t.id_tratamiento DESC");

        List<ReporteProcedimientoDTO> resultado = new ArrayList<>();
        for (Object[] r : ejecutar(sql.toString(), params)) {
            ReporteProcedimientoDTO dto = new ReporteProcedimientoDTO();
            dto.setIdTratamiento(entero(r[0]));
            dto.setPaciente(texto(r[1]));
            dto.setProcedimiento(texto(r[2]));
            dto.setCategoria(texto(r[3]));
            dto.setFechaProgramada(fecha(r[4]));
            dto.setOdontologo(texto(r[5]));
            dto.setCostoTotal(decimal(r[6]));
            dto.setEstadoAvance(texto(r[7]));
            dto.setEstadoPago(texto(r[8]));
            resultado.add(dto);
        }
        return resultado;
    }

    /** Bitácora de actividad. No incluye ninguna columna sensible de usuarios (solo nombre_completo). */
    @Transactional(readOnly = true)
    public List<ReporteActividadDTO> reporteActividad(LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);

        StringBuilder sql = new StringBuilder("""
                SELECT b.id_bitacora, u.nombre_completo, b.accion, m.nombre_modulo, b.fecha_hora, b.direccion_ip
                FROM clinica.bitacora_actividad b
                LEFT JOIN clinica.usuarios u ON u.id_usuario = b.id_usuario
                LEFT JOIN clinica.modulos_sistema m ON m.id_modulo = b.id_modulo
                WHERE 1 = 1
                """);
        Map<String, Object> params = new HashMap<>();
        if (desde != null) {
            sql.append(" AND b.fecha_hora >= :desde");
            params.put("desde", inicioDia(desde));
        }
        if (hasta != null) {
            sql.append(" AND b.fecha_hora <= :hasta");
            params.put("hasta", finDia(hasta));
        }
        sql.append(" ORDER BY b.fecha_hora DESC, b.id_bitacora DESC");

        List<ReporteActividadDTO> resultado = new ArrayList<>();
        for (Object[] r : ejecutar(sql.toString(), params)) {
            ReporteActividadDTO dto = new ReporteActividadDTO();
            dto.setIdBitacora(entero(r[0]));
            dto.setUsuario(texto(r[1]));
            dto.setAccion(texto(r[2]));
            dto.setModulo(texto(r[3]));
            dto.setFechaHora(fechaHora(r[4]));
            dto.setDireccionIp(texto(r[5]));
            resultado.add(dto);
        }
        return resultado;
    }

    // ------------------------------------------------------------------ Gráficos (vistas)

    @Transactional(readOnly = true)
    public List<PacientesNuevosVsRecurrentesDTO> pacientesNuevosVsRecurrentes() {
        List<PacientesNuevosVsRecurrentesDTO> resultado = new ArrayList<>();
        for (Object[] r : ejecutar("""
                SELECT mes, pacientes_nuevos, pacientes_recurrentes
                FROM clinica.vw_pacientes_nuevos_vs_recurrentes
                ORDER BY mes
                """, Map.of())) {
            PacientesNuevosVsRecurrentesDTO dto = new PacientesNuevosVsRecurrentesDTO();
            dto.setMes(fecha(r[0]));
            dto.setPacientesNuevos(largo(r[1]));
            dto.setPacientesRecurrentes(largo(r[2]));
            resultado.add(dto);
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<CargaOdontologoDTO> cargaOdontologos() {
        List<CargaOdontologoDTO> resultado = new ArrayList<>();
        for (Object[] r : ejecutar("""
                SELECT id_personal, odontologo, total_citas, horas
                FROM clinica.vw_carga_odontologos
                ORDER BY total_citas DESC, odontologo
                """, Map.of())) {
            CargaOdontologoDTO dto = new CargaOdontologoDTO();
            dto.setIdPersonal(entero(r[0]));
            dto.setOdontologo(texto(r[1]));
            dto.setTotalCitas(largo(r[2]));
            dto.setHoras(decimal(r[3]));
            resultado.add(dto);
        }
        return resultado;
    }

    // ------------------------------------------------------------------ Utilidades

    @SuppressWarnings("unchecked")
    private List<Object[]> ejecutar(String sql, Map<String, Object> params) {
        Query query = entityManager.createNativeQuery(sql);
        params.forEach(query::setParameter);
        return query.getResultList();
    }

    private static void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha 'Desde' no puede ser posterior a 'Hasta'");
        }
    }

    private static String normalizarEstado(String estado, Set<String> permitidos) {
        if (estado == null || estado.isBlank()) {
            return null;
        }
        String valor = estado.trim();
        if (!permitidos.contains(valor)) {
            throw new IllegalArgumentException("Estado no válido: " + valor);
        }
        return valor;
    }

    private static LocalDateTime inicioDia(LocalDate d) {
        return d != null ? d.atStartOfDay() : null;
    }

    /** Mismo criterio que CitaController (hasta inclusive hasta las 23:59:59). */
    private static LocalDateTime finDia(LocalDate d) {
        return d != null ? d.atTime(23, 59, 59) : null;
    }

    private static String texto(Object o) {
        return o == null ? null : o.toString();
    }

    private static Integer entero(Object o) {
        return o == null ? null : ((Number) o).intValue();
    }

    private static Long largo(Object o) {
        return o == null ? 0L : ((Number) o).longValue();
    }

    private static BigDecimal decimal(Object o) {
        if (o == null) {
            return BigDecimal.ZERO;
        }
        return o instanceof BigDecimal b ? b : new BigDecimal(o.toString());
    }

    private static LocalDate fecha(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof LocalDate d) {
            return d;
        }
        if (o instanceof java.sql.Date d) {
            return d.toLocalDate();
        }
        if (o instanceof Timestamp t) {
            return t.toLocalDateTime().toLocalDate();
        }
        if (o instanceof LocalDateTime dt) {
            return dt.toLocalDate();
        }
        return LocalDate.parse(o.toString().substring(0, 10));
    }

    private static LocalDateTime fechaHora(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof LocalDateTime dt) {
            return dt;
        }
        if (o instanceof Timestamp t) {
            return t.toLocalDateTime();
        }
        return LocalDateTime.parse(o.toString().replace(' ', 'T'));
    }
}
