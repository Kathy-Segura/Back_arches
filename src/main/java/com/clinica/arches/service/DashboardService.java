package com.clinica.arches.service;

import com.clinica.arches.dto.*;
import com.clinica.arches.model.Cita;
import com.clinica.arches.model.EstadoCita;
import com.clinica.arches.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private static final ZoneId ZONA = ZoneId.of("America/Managua");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    public static final int MESES_DEFAULT = 12;

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final TratamientoRepository tratamientoRepository;
    private final PagoRepository pagoRepository;
    private final GastoRepository gastoRepository;

    public DashboardService(CitaRepository citaRepository,
                            PacienteRepository pacienteRepository,
                            TratamientoRepository tratamientoRepository,
                            PagoRepository pagoRepository,
                            GastoRepository gastoRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.tratamientoRepository = tratamientoRepository;
        this.pagoRepository = pagoRepository;
        this.gastoRepository = gastoRepository;
    }

    /* ------------------------------ Resumen ------------------------------ */

    public DashboardResumen getResumen(int meses, int procedimientos, int odontologos) {
        int m = clamp(meses, 1, 36);
        return new DashboardResumen(
                getKpis(),
                getCitasPorMes(m),
                getProcedimientosFrecuentes(procedimientos, m),
                getIngresosVsGastos(m),
                getEstadosCitas(m),
                getAgendaHoy(),
                getCargaOdontologos(odontologos, m),
                getPacientesNuevosRecurrentes(m));
    }

    /* -------------------------------- KPIs -------------------------------- */

    public DashboardKpis getKpis() {
        LocalDate hoy = hoy();
        YearMonth mes = YearMonth.from(hoy);
        LocalDate inicioMes = mes.atDay(1);
        LocalDate finMes = mes.atEndOfMonth();

        BigDecimal ingresosMes = Objects.requireNonNullElse(
                pagoRepository.totalEntre(dtDesde(inicioMes), dtHasta(finMes)), BigDecimal.ZERO);

        return new DashboardKpis(
                citaRepository.contarEntre(dtDesde(hoy), dtHasta(hoy)),
                citaRepository.contarEntrePorEstado(dtDesde(hoy), dtHasta(hoy), est(EstadoCita.CONFIRMADA)),
                citaRepository.contarEntrePorEstado(dtDesde(hoy), dtHasta(hoy), est(EstadoCita.ATENDIDA)),
                pacienteRepository.countByEstadoExpediente("activo"),
                pacienteRepository.contarRegistradosEntre(dtDesde(inicioMes), dtHasta(finMes)),
                tratamientoRepository.countByEstadoAvance("en_proceso"),
                tratamientoRepository.countByEstadoAvanceAndEstadoPagoNot("en_proceso", "pagado"),
                ingresosMes,
                mes.toString());
    }

    /* ------------------------------ Gráficos ------------------------------ */

    public List<CitaMes> getCitasPorMes(int meses) {
        int m = clamp(meses, 1, 36);
        Map<YearMonth, CitaRepository.MesCitasRow> datos = citaRepository
                .citasPorMes(dtDesde(desde(m)), dtHasta(hasta()), est(EstadoCita.ATENDIDA))
                .stream()
                .collect(Collectors.toMap(r -> ym(r.getAnio(), r.getMes()), Function.identity()));

        return rango(m).stream().map(ym -> {
            CitaRepository.MesCitasRow r = datos.get(ym);
            return new CitaMes(ym.toString(),
                    r == null ? 0L : r.getTotal(),
                    r == null ? 0L : r.getAtendidas());
        }).toList();
    }

    public List<ProcedimientoFrecuente> getProcedimientosFrecuentes(int top, int meses) {
        int m = clamp(meses, 1, 36);
        return citaRepository.procedimientosFrecuentes(dtDesde(desde(m)), dtHasta(hasta()),
                        PageRequest.of(0, clamp(top, 1, 20)))
                .stream()
                // AJUSTAR al constructor real de ProcedimientoFrecuente
                .map(r -> new ProcedimientoFrecuente(r.getNombre(), r.getTotal()))
                .toList();
    }

    public List<IngresoGastoMes> getIngresosVsGastos(int meses) {
        int m = clamp(meses, 1, 36);
        Map<YearMonth, BigDecimal> ingresos = montos(pagoRepository.totalPorMes(dtDesde(desde(m)), dtHasta(hasta())));
        Map<YearMonth, BigDecimal> gastos = montos(gastoRepository.totalPorMes(desde(m), hasta()));

        return rango(m).stream()
                .map(ym -> new IngresoGastoMes(ym.toString(),
                        ingresos.getOrDefault(ym, BigDecimal.ZERO),
                        gastos.getOrDefault(ym, BigDecimal.ZERO)))
                .toList();
    }

    public List<EstadoCitaTotal> getEstadosCitas(int meses) {
        int m = clamp(meses, 1, 36);
        return citaRepository.estadosCitas(dtDesde(desde(m)), dtHasta(hasta())).stream()
                .map(r -> new EstadoCitaTotal(r.getEstado() == null ? "" : r.getEstado().toLowerCase(Locale.ROOT), r.getTotal()))
                .toList();
    }

    public List<AgendaCita> getAgendaHoy() {
        return citaRepository.agendaDelDia(dtDesde(hoy()), dtHasta(hoy())).stream().map(this::toAgenda).toList();
    }

    public List<CargaOdontologo> getCargaOdontologos(int top, int meses) {
        int m = clamp(meses, 1, 36);
        return citaRepository.cargaOdontologos(dtDesde(desde(m)), dtHasta(hasta()),
                        PageRequest.of(0, clamp(top, 1, 20)))
                .stream()
                // AJUSTAR al constructor real de CargaOdontologo
                .map(r -> new CargaOdontologo(
                        r.getPersonal().getNombreCompleto(),
                        r.getTotal()))
                .toList();
    }

    public List<PacientesNuevosRecurrentes> getPacientesNuevosRecurrentes(int meses) {
        int m = clamp(meses, 1, 36);
        Map<YearMonth, Long> nuevos = totales(pacienteRepository.nuevosPorMes(dtDesde(desde(m)), dtHasta(hasta())));
        Map<YearMonth, Long> recurrentes = totales(citaRepository.pacientesRecurrentesPorMes(dtDesde(desde(m)), dtHasta(hasta())));

        return rango(m).stream()
                .map(ym -> new PacientesNuevosRecurrentes(ym.toString(),
                        nuevos.getOrDefault(ym, 0L),
                        recurrentes.getOrDefault(ym, 0L)))
                .toList();
    }

    /* ------------------------------ Utilidades ------------------------------ */

    private AgendaCita toAgenda(Cita c) {
        return new AgendaCita(
                c.getIdCita().longValue(),
                c.getFechaHora().format(HORA),
                c.getDuracionMinutos(),
                c.getPaciente().getNombreCompleto(),
                c.getProcedimiento() == null ? "" : c.getProcedimiento().getNombreProcedimiento(),
                c.getPersonal().getNombreCompleto(),
                c.getEstadoCita().toLowerCase(Locale.ROOT));
    }

    private static LocalDate hoy() {
        return LocalDate.now(ZONA);
    }

    /** Inicio del día (inclusive) para consultas sobre LocalDateTime. */
    private static LocalDateTime dtDesde(LocalDate d) {
        return d.atStartOfDay();
    }

    /** Fin EXCLUSIVO: medianoche del día siguiente. */
    private static LocalDateTime dtHasta(LocalDate d) {
        return d.plusDays(1).atStartOfDay();
    }

    /** Primer día del mes más antiguo del rango (incluye el mes actual). */
    private static LocalDate desde(int meses) {
        return YearMonth.from(hoy()).minusMonths(meses - 1L).atDay(1);
    }

    private static LocalDate hasta() {
        return YearMonth.from(hoy()).atEndOfMonth();
    }

    /** Lista continua de meses, del más antiguo al actual, para no dejar huecos en las gráficas. */
    private static List<YearMonth> rango(int meses) {
        YearMonth fin = YearMonth.from(hoy());
        return IntStream.range(0, meses)
                .mapToObj(i -> fin.minusMonths(meses - 1L - i))
                .toList();
    }

    private static YearMonth ym(Integer anio, Integer mes) {
        try {
            return YearMonth.of(anio, mes);
        } catch (DateTimeException e) {
            throw new IllegalStateException("Año/mes inválido en el resultado de la consulta", e);
        }
    }

    private static Map<YearMonth, Long> totales(List<MesTotalRow> filas) {
        return filas.stream().collect(Collectors.toMap(r -> ym(r.getAnio(), r.getMes()), MesTotalRow::getTotal));
    }

    private static Map<YearMonth, BigDecimal> montos(List<MesMontoRow> filas) {
        return filas.stream().collect(Collectors.toMap(r -> ym(r.getAnio(), r.getMes()), MesMontoRow::getTotal));
    }

    /** Estado como se guarda en BD: minúsculas (atendida, no_asistio...). */
    private static String est(EstadoCita e) {
        return e.name().toLowerCase(Locale.ROOT);
    }

    private static int clamp(int valor, int min, int max) {
        return Math.max(min, Math.min(max, valor));
    }
}
