package com.clinica.arches.service;

import com.clinica.arches.dto.CitaCancelarRequest;
import com.clinica.arches.dto.CitaDTO;
import com.clinica.arches.dto.CitaRequest;
import com.clinica.arches.dto.MotivoCancelacionDTO;
import com.clinica.arches.exception.RecursoNoEncontradoException;
import com.clinica.arches.model.CatalogoMotivoCancelacion;
import com.clinica.arches.model.CatalogoProcedimiento;
import com.clinica.arches.model.Cita;
import com.clinica.arches.model.Paciente;
import com.clinica.arches.model.Personal;
import com.clinica.arches.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.clinica.arches.repository.CitaSpecification;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final PersonalRepository personalRepository;
    private final ProcedimientoRepository procedimientoRepository;
    private final MotivoCancelacionRepository motivoRepository;

    public CitaService(CitaRepository citaRepository,
                       PacienteRepository pacienteRepository,
                       PersonalRepository personalRepository,
                       ProcedimientoRepository procedimientoRepository,
                       MotivoCancelacionRepository motivoRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.personalRepository = personalRepository;
        this.procedimientoRepository = procedimientoRepository;
        this.motivoRepository = motivoRepository;
    }

    /** Sustenta la pestaña "Listado" (búsqueda, filtros de odontólogo/estado/fecha, paginación). */
    public Page<CitaDTO> listarConFiltros(String search, Integer idPersonal, String estado,
                                          LocalDateTime desde, LocalDateTime hasta, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaHora"));
        String searchNormalizado = (search == null || search.isBlank()) ? null : search.trim();
        String estadoNormalizado = (estado == null || estado.isBlank()) ? null : estado.trim();
        return citaRepository.findAll(
                        CitaSpecification.conFiltros(searchNormalizado, idPersonal, estadoNormalizado, desde, hasta),
                        pageable)
                .map(this::convertir);
    }

    /** Misma búsqueda sin paginar, para exportaciones futuras (Excel/PDF). */
    public List<CitaDTO> listarConFiltrosSinPaginar(String search, Integer idPersonal, String estado,
                                                    LocalDateTime desde, LocalDateTime hasta) {
        String searchNormalizado = (search == null || search.isBlank()) ? null : search.trim();
        String estadoNormalizado = (estado == null || estado.isBlank()) ? null : estado.trim();
        Sort sort = Sort.by(Sort.Direction.ASC, "fechaHora");
        return citaRepository.findAll(
                        CitaSpecification.conFiltros(searchNormalizado, idPersonal, estadoNormalizado, desde, hasta),
                        sort)
                .stream()
                .map(this::convertir)
                .collect(Collectors.toList());
    }

    /** Sustenta la pestaña "Calendario": todas las citas de un rango de fechas, sin paginar. */
    public List<CitaDTO> listarParaCalendario(Integer idPersonal, LocalDateTime desde, LocalDateTime hasta) {
        return citaRepository.buscarPorRango(idPersonal, desde, hasta).stream()
                .map(this::convertir)
                .collect(Collectors.toList());
    }

    public CitaDTO buscarPorId(Integer id) {
        return convertir(obtenerEntidad(id));
    }

    public CitaDTO crear(CitaRequest request) {
        validarDatosBasicos(request);
        Cita cita = new Cita();
        aplicarDatos(cita, request, null);
        cita.setEstadoCita("programada");
        cita.setFechaCreacion(LocalDateTime.now());
        return convertir(citaRepository.save(cita));
    }

    public CitaDTO actualizar(Integer id, CitaRequest request) {
        validarDatosBasicos(request);
        Cita existente = obtenerEntidad(id);
        aplicarDatos(existente, request, id);
        return convertir(citaRepository.save(existente));
    }

    public CitaDTO marcarAtendida(Integer id) {
        Cita cita = obtenerEntidad(id);
        cita.setEstadoCita("atendida");
        return convertir(citaRepository.save(cita));
    }

    public CitaDTO confirmar(Integer id) {
        Cita cita = obtenerEntidad(id);
        cita.setEstadoCita("confirmada");
        return convertir(citaRepository.save(cita));
    }

    public CitaDTO cancelar(Integer id, CitaCancelarRequest request) {
        Cita cita = obtenerEntidad(id);
        cita.setEstadoCita("cancelada");
        if (request != null && request.getIdMotivo() != null) {
            CatalogoMotivoCancelacion motivo = motivoRepository.findById(request.getIdMotivo())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Motivo de cancelación no encontrado: " + request.getIdMotivo()));
            cita.setMotivo(motivo);
        }
        if (request != null && request.getNotas() != null && !request.getNotas().isBlank()) {
            cita.setNotas(request.getNotas().trim());
        }
        return convertir(citaRepository.save(cita));
    }

    /** Sustenta el selector de motivo dentro del diálogo de cancelación. */
    public List<MotivoCancelacionDTO> listarMotivosCancelacion() {
        return motivoRepository.findAllByOrderByNombreMotivoAsc().stream()
                .map(m -> {
                    MotivoCancelacionDTO dto = new MotivoCancelacionDTO();
                    dto.setIdMotivo(m.getIdMotivo());
                    dto.setNombreMotivo(m.getNombreMotivo());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void eliminar(Integer id) {
        if (!citaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Cita no encontrada: " + id);
        }
        citaRepository.deleteById(id);
    }

    // ------------------------------------------------------------------------------------------------//

    private Cita obtenerEntidad(Integer id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada: " + id));
    }

    private void validarDatosBasicos(CitaRequest request) {
        if (request.getIdPaciente() == null) throw new IllegalArgumentException("Debe seleccionar un paciente");
        if (request.getIdPersonal() == null) throw new IllegalArgumentException("Debe seleccionar un odontólogo");
        if (request.getFechaHora() == null) throw new IllegalArgumentException("Debe indicar fecha y hora de la cita");
    }

    private void aplicarDatos(Cita cita, CitaRequest request, Integer idCitaExcluir) {
        Paciente paciente = pacienteRepository.findById(request.getIdPaciente())
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado: " + request.getIdPaciente()));
        Personal personal = personalRepository.findById(request.getIdPersonal())
                .orElseThrow(() -> new RecursoNoEncontradoException("Odontólogo no encontrado: " + request.getIdPersonal()));

        if (!"Odontólogo".equals(personal.getCargo())) {
            throw new IllegalArgumentException("El personal seleccionado no es un odontólogo");
        }

        if (citaRepository.existeChoqueHorario(personal.getIdPersonal(), request.getFechaHora(), idCitaExcluir)) {
            throw new IllegalStateException("El odontólogo ya tiene una cita programada en ese horario");
        }

        cita.setPaciente(paciente);
        cita.setPersonal(personal);

        if (request.getIdProcedimiento() != null) {
            CatalogoProcedimiento procedimiento = procedimientoRepository.findById(request.getIdProcedimiento())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Procedimiento no encontrado: " + request.getIdProcedimiento()));
            cita.setProcedimiento(procedimiento);
        } else {
            cita.setProcedimiento(null);
        }

        cita.setFechaHora(request.getFechaHora());
        cita.setDuracionMinutos(request.getDuracionMinutos() != null ? request.getDuracionMinutos() : 30);
        cita.setNotas(request.getNotas());
    }

    private CitaDTO convertir(Cita c) {
        CitaDTO dto = new CitaDTO();
        dto.setIdCita(c.getIdCita());

        dto.setIdPaciente(c.getPaciente().getIdPaciente());
        dto.setNombrePaciente(c.getPaciente().getNombreCompleto());

        dto.setIdPersonal(c.getPersonal().getIdPersonal());
        dto.setNombrePersonal(c.getPersonal().getNombreCompleto());

        if (c.getProcedimiento() != null) {
            dto.setIdProcedimiento(c.getProcedimiento().getIdProcedimiento());
            dto.setNombreProcedimiento(c.getProcedimiento().getNombreProcedimiento());
        }

        dto.setFechaHora(c.getFechaHora());
        dto.setDuracionMinutos(c.getDuracionMinutos());
        dto.setEstadoCita(c.getEstadoCita());

        if (c.getMotivo() != null) {
            dto.setIdMotivo(c.getMotivo().getIdMotivo());
            dto.setNombreMotivo(c.getMotivo().getNombreMotivo());
        }

        dto.setNotas(c.getNotas());
        return dto;
    }
}
