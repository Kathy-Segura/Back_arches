package com.clinica.arches.service;

import com.clinica.arches.dto.*;
import com.clinica.arches.exception.RecursoNoEncontradoException;
import com.clinica.arches.model.*;
import com.clinica.arches.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Módulo Expediente clínico: historia clínica, diagnósticos, evolución, odontograma
 * y vista de solo lectura del plan de tratamiento.
 *
 * Los antecedentes (alergias, enfermedades crónicas, etc.) NO se gestionan aquí: ya existen
 * en el módulo de Pacientes (tabla antecedentes_paciente).
 */
@Service
@Transactional(readOnly = true)
public class ExpedienteService {

    private static final ZoneId ZONA = ZoneId.of("America/Managua");
    private static final String CARGO_ODONTOLOGO = "Odontólogo";

    private static final int MAX_DIAGNOSTICO = 150;
    private static final int MAX_DESCRIPCION = 300;
    private static final int MAX_OBSERVACIONES = 300;

    private final PacienteRepository pacienteRepository;
    private final PersonalRepository personalRepository;
    private final HistoriaClinicaRepository historiaRepository;
    private final DiagnosticoRepository diagnosticoRepository;
    private final EvolucionClinicaRepository evolucionRepository;
    private final CatalogoHallazgoDentalRepository hallazgoRepository;
    private final OdontogramaPiezaRepository piezaRepository;
    private final OdontogramaHistorialHallazgoRepository historialRepository;
    private final ExpedienteVistaRepository vistaRepository;
    private final TratamientoRepository tratamientoRepository;
    private final ProcedimientoRepository procedimientoRepository;

    public ExpedienteService(PacienteRepository pacienteRepository,
                             PersonalRepository personalRepository,
                             HistoriaClinicaRepository historiaRepository,
                             DiagnosticoRepository diagnosticoRepository,
                             EvolucionClinicaRepository evolucionRepository,
                             CatalogoHallazgoDentalRepository hallazgoRepository,
                             OdontogramaPiezaRepository piezaRepository,
                             OdontogramaHistorialHallazgoRepository historialRepository,
                             ExpedienteVistaRepository vistaRepository,
                             TratamientoRepository tratamientoRepository,
                             ProcedimientoRepository procedimientoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.personalRepository = personalRepository;
        this.historiaRepository = historiaRepository;
        this.diagnosticoRepository = diagnosticoRepository;
        this.evolucionRepository = evolucionRepository;
        this.hallazgoRepository = hallazgoRepository;
        this.piezaRepository = piezaRepository;
        this.historialRepository = historialRepository;
        this.vistaRepository = vistaRepository;
        this.tratamientoRepository = tratamientoRepository;
        this.procedimientoRepository = procedimientoRepository;
    }

    /* ============================ Resumen ============================ */

    public ExpedienteResumenDTO obtenerResumen(Integer idPaciente) {
        ExpedienteVistaRepository.ResumenRow r = vistaRepository.resumen(idPaciente)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado: " + idPaciente));
        ExpedienteResumenDTO dto = new ExpedienteResumenDTO();
        dto.setIdPaciente(r.getIdPaciente());
        dto.setNombreCompleto(r.getNombreCompleto());
        dto.setEdad(r.getEdad());
        dto.setEstadoExpediente(r.getEstadoExpediente());
        dto.setHistoriaClinicaActualizada(r.getHistoriaClinicaActualizada());
        dto.setTotalDiagnosticos(r.getTotalDiagnosticos() == null ? 0L : r.getTotalDiagnosticos());
        dto.setTotalNotasEvolucion(r.getTotalNotasEvolucion() == null ? 0L : r.getTotalNotasEvolucion());
        dto.setTotalCitasAtendidas(r.getTotalCitasAtendidas() == null ? 0L : r.getTotalCitasAtendidas());
        return dto;
    }

    /* ========================= Historia clínica ========================= */

    public HistoriaClinicaDTO obtenerHistoria(Integer idPaciente) {
        verificarPaciente(idPaciente);
        return historiaRepository.findFirstByIdPacienteOrderByIdHistoriaDesc(idPaciente)
                .map(this::convertir)
                .orElseGet(() -> {
                    HistoriaClinicaDTO vacia = new HistoriaClinicaDTO();
                    vacia.setIdPaciente(idPaciente);
                    return vacia;
                });
    }

    /** Crea o actualiza la única historia del paciente (uk_historiaclinica_paciente). */
    @Transactional
    public HistoriaClinicaDTO guardarHistoria(Integer idPaciente, HistoriaClinicaRequest request) {
        verificarPaciente(idPaciente);
        if (request == null) throw new IllegalArgumentException("Faltan los datos de la historia clínica");

        HistoriaClinica historia = historiaRepository.findFirstByIdPacienteOrderByIdHistoriaDesc(idPaciente)
                .orElseGet(() -> {
                    HistoriaClinica nueva = new HistoriaClinica();
                    nueva.setIdPaciente(idPaciente);
                    return nueva;
                });
        historia.setAntecedentesMedicos(limpiar(request.getAntecedentesMedicos()));
        historia.setAntecedentesOdontologicos(limpiar(request.getAntecedentesOdontologicos()));
        historia.setHabitos(limpiar(request.getHabitos()));
        historia.setFechaActualizacion(LocalDateTime.now(ZONA));
        return convertir(historiaRepository.save(historia));
    }

    /* ============================ Diagnósticos ============================ */

    public List<DiagnosticoDTO> listarDiagnosticos(Integer idPaciente) {
        verificarPaciente(idPaciente);
        List<Diagnostico> lista = diagnosticoRepository
                .findByIdPacienteOrderByFechaDiagnosticoDescIdDiagnosticoDesc(idPaciente);
        Map<Integer, String> nombres = nombresPersonal(
                lista.stream().map(Diagnostico::getIdPersonal).collect(Collectors.toSet()));
        return lista.stream()
                .map(d -> convertir(d, nombres.get(d.getIdPersonal())))
                .collect(Collectors.toList());
    }

    @Transactional
    public DiagnosticoDTO crearDiagnostico(Integer idPaciente, DiagnosticoRequest request) {
        verificarPaciente(idPaciente);
        Diagnostico d = new Diagnostico();
        d.setIdPaciente(idPaciente);
        Personal autor = aplicar(d, request);
        return convertir(diagnosticoRepository.save(d), autor.getNombreCompleto());
    }

    @Transactional
    public DiagnosticoDTO actualizarDiagnostico(Integer idPaciente, Integer idDiagnostico, DiagnosticoRequest request) {
        Diagnostico d = obtenerDiagnostico(idPaciente, idDiagnostico);
        Personal autor = aplicar(d, request);
        return convertir(diagnosticoRepository.save(d), autor.getNombreCompleto());
    }

    @Transactional
    public void eliminarDiagnostico(Integer idPaciente, Integer idDiagnostico) {
        diagnosticoRepository.delete(obtenerDiagnostico(idPaciente, idDiagnostico));
    }

    private Personal aplicar(Diagnostico d, DiagnosticoRequest request) {
        if (request == null) throw new IllegalArgumentException("Faltan los datos del diagnóstico");
        Personal autor = validarOdontologo(request.getIdPersonal());
        String texto = requerido(request.getDiagnostico(), "El diagnóstico es obligatorio", MAX_DIAGNOSTICO,
                "El diagnóstico no puede superar " + MAX_DIAGNOSTICO + " caracteres");
        String descripcion = limpiar(request.getDescripcion());
        if (descripcion != null && descripcion.length() > MAX_DESCRIPCION) {
            throw new IllegalArgumentException("La descripción no puede superar " + MAX_DESCRIPCION + " caracteres");
        }
        d.setIdPersonal(autor.getIdPersonal());
        d.setDiagnostico(texto);
        d.setDescripcion(descripcion);
        d.setFechaDiagnostico(request.getFechaDiagnostico() != null ? request.getFechaDiagnostico() : LocalDate.now(ZONA));
        return autor;
    }

    private Diagnostico obtenerDiagnostico(Integer idPaciente, Integer idDiagnostico) {
        return diagnosticoRepository.findById(idDiagnostico)
                .filter(d -> d.getIdPaciente().equals(idPaciente))
                .orElseThrow(() -> new RecursoNoEncontradoException("Diagnóstico no encontrado: " + idDiagnostico));
    }

    /* ======================== Evolución y notas ======================== */

    public List<EvolucionDTO> listarEvolucion(Integer idPaciente) {
        verificarPaciente(idPaciente);
        List<EvolucionClinica> lista = evolucionRepository
                .findByIdPacienteOrderByFechaRegistroDescIdEvolucionDesc(idPaciente);
        Map<Integer, String> nombres = nombresPersonal(
                lista.stream().map(EvolucionClinica::getIdPersonal).collect(Collectors.toSet()));
        return lista.stream()
                .map(e -> convertir(e, nombres.get(e.getIdPersonal())))
                .collect(Collectors.toList());
    }

    @Transactional
    public EvolucionDTO crearEvolucion(Integer idPaciente, EvolucionRequest request) {
        verificarPaciente(idPaciente);
        if (request == null) throw new IllegalArgumentException("Faltan los datos de la nota");
        Personal autor = validarOdontologo(request.getIdPersonal());
        EvolucionClinica e = new EvolucionClinica();
        e.setIdPaciente(idPaciente);
        e.setIdPersonal(autor.getIdPersonal());
        e.setNota(requerido(request.getNota(), "La nota es obligatoria", Integer.MAX_VALUE, ""));
        e.setFechaRegistro(LocalDateTime.now(ZONA));
        return convertir(evolucionRepository.save(e), autor.getNombreCompleto());
    }

    /** Edita solo el texto de la nota; conserva autor original y fecha de registro. */
    @Transactional
    public EvolucionDTO actualizarEvolucion(Integer idPaciente, Integer idEvolucion, EvolucionRequest request) {
        if (request == null) throw new IllegalArgumentException("Faltan los datos de la nota");
        EvolucionClinica e = obtenerEvolucion(idPaciente, idEvolucion);
        e.setNota(requerido(request.getNota(), "La nota es obligatoria", Integer.MAX_VALUE, ""));
        EvolucionClinica guardada = evolucionRepository.save(e);
        return convertir(guardada, nombresPersonal(List.of(guardada.getIdPersonal())).get(guardada.getIdPersonal()));
    }

    @Transactional
    public void eliminarEvolucion(Integer idPaciente, Integer idEvolucion) {
        evolucionRepository.delete(obtenerEvolucion(idPaciente, idEvolucion));
    }

    private EvolucionClinica obtenerEvolucion(Integer idPaciente, Integer idEvolucion) {
        return evolucionRepository.findById(idEvolucion)
                .filter(e -> e.getIdPaciente().equals(idPaciente))
                .orElseThrow(() -> new RecursoNoEncontradoException("Nota de evolución no encontrada: " + idEvolucion));
    }

    /* ============================ Odontograma ============================ */

    public List<HallazgoTipoDTO> listarTiposHallazgo() {
        return hallazgoRepository.findAll(Sort.by("nombreHallazgo")).stream()
                .map(this::convertir)
                .collect(Collectors.toList());
    }

    /** Estado actual de cada pieza que tiene un hallazgo (vw_odontograma_actual). */
    public List<OdontogramaPiezaDTO> odontogramaActual(Integer idPaciente) {
        verificarPaciente(idPaciente);
        return vistaRepository.odontogramaActual(idPaciente).stream()
                .map(this::convertir)
                .collect(Collectors.toList());
    }

    public List<HallazgoHistorialDTO> historialPieza(Integer idPaciente, Integer numeroPieza) {
        verificarPaciente(idPaciente);
        validarNumeroPieza(numeroPieza);
        return piezaRepository.findByIdPacienteAndNumeroPieza(idPaciente, numeroPieza.shortValue())
                .map(pieza -> {
                    List<OdontogramaHistorialHallazgo> registros =
                            historialRepository.findByIdPiezaOrderByFechaRegistroDescIdRegistroDesc(pieza.getIdPieza());
                    Map<Integer, CatalogoHallazgoDental> tipos = hallazgoRepository
                            .findAllById(registros.stream().map(OdontogramaHistorialHallazgo::getIdHallazgoTipo)
                                    .collect(Collectors.toSet()))
                            .stream().collect(Collectors.toMap(CatalogoHallazgoDental::getIdHallazgoTipo, t -> t));
                    Map<Integer, String> nombres = nombresPersonal(
                            registros.stream().map(OdontogramaHistorialHallazgo::getIdPersonal).collect(Collectors.toSet()));
                    return registros.stream()
                            .map(r -> convertir(r, numeroPieza, tipos.get(r.getIdHallazgoTipo()), nombres.get(r.getIdPersonal())))
                            .collect(Collectors.toList());
                })
                .orElseGet(List::of);
    }

    /**
     * Registra un hallazgo en una pieza. Crea la fila de odontograma_pieza si aún no existe e inserta
     * el historial; el trigger de la BD actualiza id_hallazgo_tipo_actual de la pieza.
     */
    @Transactional
    public OdontogramaPiezaDTO registrarHallazgo(Integer idPaciente, Integer numeroPieza, HallazgoRegistroRequest request) {
        verificarPaciente(idPaciente);
        validarNumeroPieza(numeroPieza);
        if (request == null) throw new IllegalArgumentException("Faltan los datos del hallazgo");
        if (request.getIdHallazgoTipo() == null) throw new IllegalArgumentException("Debe seleccionar un hallazgo");
        CatalogoHallazgoDental tipo = hallazgoRepository.findById(request.getIdHallazgoTipo())
                .orElseThrow(() -> new RecursoNoEncontradoException("Hallazgo no encontrado: " + request.getIdHallazgoTipo()));
        Personal autor = validarOdontologo(request.getIdPersonal());
        String observaciones = limpiar(request.getObservaciones());
        if (observaciones != null && observaciones.length() > MAX_OBSERVACIONES) {
            throw new IllegalArgumentException("Las observaciones no pueden superar " + MAX_OBSERVACIONES + " caracteres");
        }

        OdontogramaPieza pieza = piezaRepository
                .findByIdPacienteAndNumeroPieza(idPaciente, numeroPieza.shortValue())
                .orElseGet(() -> {
                    OdontogramaPieza nueva = new OdontogramaPieza();
                    nueva.setIdPaciente(idPaciente);
                    nueva.setNumeroPieza(numeroPieza.shortValue());
                    nueva.setFechaActualizacion(LocalDateTime.now(ZONA));
                    return piezaRepository.save(nueva);
                });

        OdontogramaHistorialHallazgo registro = new OdontogramaHistorialHallazgo();
        registro.setIdPieza(pieza.getIdPieza());
        registro.setIdHallazgoTipo(tipo.getIdHallazgoTipo());
        registro.setIdPersonal(autor.getIdPersonal());
        registro.setFechaRegistro(LocalDateTime.now(ZONA));
        registro.setObservaciones(observaciones);
        historialRepository.save(registro);

        return vistaRepository.odontogramaActual(idPaciente).stream()
                .filter(r -> numeroPieza.equals(r.getNumeroPieza()))
                .findFirst()
                .map(this::convertir)
                .orElseGet(() -> {
                    OdontogramaPiezaDTO dto = new OdontogramaPiezaDTO();
                    dto.setNumeroPieza(numeroPieza);
                    dto.setCodigoHallazgo(tipo.getCodigoHallazgo());
                    dto.setNombreHallazgo(tipo.getNombreHallazgo());
                    dto.setColorRepresentativo(tipo.getColorRepresentativo());
                    dto.setFechaActualizacion(registro.getFechaRegistro());
                    return dto;
                });
    }

    /* ======================= Plan de tratamiento ======================= */

    /** Lectura de los tratamientos del paciente (los administra el módulo Tratamientos). */
    public List<PlanTratamientoDTO> planTratamiento(Integer idPaciente) {
        verificarPaciente(idPaciente);
        List<Tratamiento> lista = tratamientoRepository
                .buscar(idPaciente, null, null, null, PageRequest.of(0, 100))
                .getContent();

        Map<Integer, String> procedimientos = new HashMap<>();
        procedimientoRepository
                .findAllById(lista.stream().map(Tratamiento::getIdProcedimiento).collect(Collectors.toSet()))
                .forEach(p -> procedimientos.put(p.getIdProcedimiento(), p.getNombreProcedimiento()));
        Map<Integer, String> odontologos = nombresPersonal(
                lista.stream().map(Tratamiento::getIdPersonal).collect(Collectors.toSet()));

        return lista.stream().map(t -> {
            PlanTratamientoDTO dto = new PlanTratamientoDTO();
            dto.setIdTratamiento(t.getIdTratamiento());
            dto.setIdProcedimiento(t.getIdProcedimiento());
            dto.setNombreProcedimiento(procedimientos.get(t.getIdProcedimiento()));
            dto.setIdPersonal(t.getIdPersonal());
            dto.setNombrePersonal(odontologos.get(t.getIdPersonal()));
            dto.setSesionesPlanificadas(t.getSesionesPlanificadas());
            dto.setCostoTotal(t.getCostoTotal());
            dto.setEstadoAvance(t.getEstadoAvance());
            dto.setEstadoPago(t.getEstadoPago());
            dto.setFechaProgramada(t.getFechaProgramada());
            return dto;
        }).collect(Collectors.toList());
    }

    /* ============================ Utilidades ============================ */

    private void verificarPaciente(Integer idPaciente) {
        if (idPaciente == null || !pacienteRepository.existsById(idPaciente)) {
            throw new RecursoNoEncontradoException("Paciente no encontrado: " + idPaciente);
        }
    }

    private Personal validarOdontologo(Integer idPersonal) {
        if (idPersonal == null) throw new IllegalArgumentException("Debe seleccionar un odontólogo");
        Personal personal = personalRepository.findById(idPersonal)
                .orElseThrow(() -> new RecursoNoEncontradoException("Odontólogo no encontrado: " + idPersonal));
        if (!CARGO_ODONTOLOGO.equals(personal.getCargo())) {
            throw new IllegalArgumentException("El personal seleccionado no es un odontólogo");
        }
        return personal;
    }

    /** Numeración FDI de dentición permanente, igual que ck_odontogramapieza_numero_fdi: 11-18, 21-28, 31-38, 41-48. */
    private void validarNumeroPieza(Integer numeroPieza) {
        if (numeroPieza == null
                || numeroPieza / 10 < 1 || numeroPieza / 10 > 4
                || numeroPieza % 10 < 1 || numeroPieza % 10 > 8) {
            throw new IllegalArgumentException("Número de pieza inválido (use numeración FDI: 11-18, 21-28, 31-38, 41-48)");
        }
    }

    private Map<Integer, String> nombresPersonal(Collection<Integer> ids) {
        if (ids.isEmpty()) return Map.of();
        return personalRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Personal::getIdPersonal, Personal::getNombreCompleto));
    }

    private static String limpiar(String texto) {
        if (texto == null) return null;
        String t = texto.trim();
        return t.isEmpty() ? null : t;
    }

    private static String requerido(String texto, String mensajeVacio, int max, String mensajeLargo) {
        String t = limpiar(texto);
        if (t == null) throw new IllegalArgumentException(mensajeVacio);
        if (t.length() > max) throw new IllegalArgumentException(mensajeLargo);
        return t;
    }

    /* ============================ Conversión ============================ */

    private HistoriaClinicaDTO convertir(HistoriaClinica h) {
        HistoriaClinicaDTO dto = new HistoriaClinicaDTO();
        dto.setIdHistoria(h.getIdHistoria());
        dto.setIdPaciente(h.getIdPaciente());
        dto.setAntecedentesMedicos(h.getAntecedentesMedicos());
        dto.setAntecedentesOdontologicos(h.getAntecedentesOdontologicos());
        dto.setHabitos(h.getHabitos());
        dto.setFechaActualizacion(h.getFechaActualizacion());
        return dto;
    }

    private DiagnosticoDTO convertir(Diagnostico d, String nombrePersonal) {
        DiagnosticoDTO dto = new DiagnosticoDTO();
        dto.setIdDiagnostico(d.getIdDiagnostico());
        dto.setIdPaciente(d.getIdPaciente());
        dto.setIdPersonal(d.getIdPersonal());
        dto.setNombrePersonal(nombrePersonal);
        dto.setDiagnostico(d.getDiagnostico());
        dto.setDescripcion(d.getDescripcion());
        dto.setFechaDiagnostico(d.getFechaDiagnostico());
        return dto;
    }

    private EvolucionDTO convertir(EvolucionClinica e, String nombrePersonal) {
        EvolucionDTO dto = new EvolucionDTO();
        dto.setIdEvolucion(e.getIdEvolucion());
        dto.setIdPaciente(e.getIdPaciente());
        dto.setIdPersonal(e.getIdPersonal());
        dto.setNombrePersonal(nombrePersonal);
        dto.setNota(e.getNota());
        dto.setFechaRegistro(e.getFechaRegistro());
        return dto;
    }

    private HallazgoTipoDTO convertir(CatalogoHallazgoDental t) {
        HallazgoTipoDTO dto = new HallazgoTipoDTO();
        dto.setIdHallazgoTipo(t.getIdHallazgoTipo());
        dto.setCodigoHallazgo(t.getCodigoHallazgo());
        dto.setNombreHallazgo(t.getNombreHallazgo());
        dto.setColorRepresentativo(t.getColorRepresentativo());
        return dto;
    }

    private OdontogramaPiezaDTO convertir(ExpedienteVistaRepository.PiezaRow r) {
        OdontogramaPiezaDTO dto = new OdontogramaPiezaDTO();
        dto.setNumeroPieza(r.getNumeroPieza());
        dto.setCodigoHallazgo(r.getCodigoHallazgo());
        dto.setNombreHallazgo(r.getNombreHallazgo());
        dto.setColorRepresentativo(r.getColorRepresentativo());
        dto.setFechaActualizacion(r.getFechaActualizacion());
        return dto;
    }

    private HallazgoHistorialDTO convertir(OdontogramaHistorialHallazgo r, Integer numeroPieza,
                                           CatalogoHallazgoDental tipo, String nombrePersonal) {
        HallazgoHistorialDTO dto = new HallazgoHistorialDTO();
        dto.setIdRegistro(r.getIdRegistro());
        dto.setNumeroPieza(numeroPieza);
        dto.setIdHallazgoTipo(r.getIdHallazgoTipo());
        if (tipo != null) {
            dto.setCodigoHallazgo(tipo.getCodigoHallazgo());
            dto.setNombreHallazgo(tipo.getNombreHallazgo());
            dto.setColorRepresentativo(tipo.getColorRepresentativo());
        }
        dto.setIdPersonal(r.getIdPersonal());
        dto.setNombrePersonal(nombrePersonal);
        dto.setFechaRegistro(r.getFechaRegistro());
        dto.setObservaciones(r.getObservaciones());
        return dto;
    }
}
