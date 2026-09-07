package com.clinica.arches.service;

import com.clinica.arches.dto.AntecedenteDTO;
import com.clinica.arches.dto.ContactoEmergenciaDTO;
import com.clinica.arches.dto.PacienteDTO;
import com.clinica.arches.model.Paciente;
import com.clinica.arches.repository.AntecedentePacienteRepository;
import com.clinica.arches.repository.ContactoEmergenciaRepository;
import com.clinica.arches.repository.PacienteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final ContactoEmergenciaRepository contactoRepository;
    private final AntecedentePacienteRepository antecedenteRepository;

    public PacienteService(PacienteRepository pacienteRepository,
                           ContactoEmergenciaRepository contactoRepository,
                           AntecedentePacienteRepository antecedenteRepository) {
        this.pacienteRepository = pacienteRepository;
        this.contactoRepository = contactoRepository;
        this.antecedenteRepository = antecedenteRepository;
    }

    // Listado liviano sin filtros (se deja por compatibilidad; el listado de la UI usa listarConFiltros)
    public List<PacienteDTO> listarTodos() {
        return pacienteRepository.findAll().stream()
                .map(this::convertirBasico)
                .collect(Collectors.toList());
    }

    /**
     * Sustenta: barra de búsqueda (nombre o cédula), filtro de estado (activo/inactivo)
     * y la tabla paginada del listado de pacientes.
     */
    public Page<PacienteDTO> listarConFiltros(String search, String estado, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "nombreCompleto"));
        String searchNormalizado = (search == null || search.isBlank()) ? null : search.trim();
        String estadoNormalizado = (estado == null || estado.isBlank()) ? null : estado.trim();
        return pacienteRepository.buscarConFiltros(searchNormalizado, estadoNormalizado, pageable)
                .map(this::convertirBasico);
    }

    // Usado por los endpoints de exportación: misma lógica de filtro, pero sin paginar
    public List<Paciente> listarEntidadesConFiltros(String search, String estado) {
        String searchNormalizado = (search == null || search.isBlank()) ? null : search.trim();
        String estadoNormalizado = (estado == null || estado.isBlank()) ? null : estado.trim();
        return pacienteRepository.buscarConFiltrosSinPaginar(searchNormalizado, estadoNormalizado);
    }

    // Detalle completo: incluye contactos y antecedentes. Sustenta el botón "Ver" y la ficha PDF.
    public PacienteDTO buscarPorId(Integer id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado: " + id));
        return convertirCompleto(paciente);
    }

    public PacienteDTO crear(Paciente paciente) {
        paciente.setIdPaciente(null); //fuerza persist() para que se autogeneren los ids y no los envie el cliente
        paciente.setEstadoExpediente("activo");
        paciente.setFechaRegistro(LocalDateTime.now());
        return convertirBasico(pacienteRepository.save(paciente));
    }

    public PacienteDTO actualizar(Integer id, Paciente datos) {
        Paciente existente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado: " + id));
        BeanUtils.copyProperties(datos, existente, "idPaciente", "fechaRegistro", "estadoExpediente");
        return convertirBasico(pacienteRepository.save(existente));
    }

    // Soft delete: delega en el procedimiento almacenado (no hay DELETE físico)
    public void archivar(Integer id) {
        pacienteRepository.archivarPaciente(id);
    }

    private PacienteDTO convertirBasico(Paciente p) {
        PacienteDTO dto = new PacienteDTO();
        BeanUtils.copyProperties(p, dto);
        dto.setEdad(Period.between(p.getFechaNacimiento(), LocalDate.now()).getYears());
        return dto;
    }

    private PacienteDTO convertirCompleto(Paciente p) {
        PacienteDTO dto = convertirBasico(p);

        dto.setContactos(contactoRepository.findByIdPaciente(p.getIdPaciente()).stream()
                .map(c -> {
                    ContactoEmergenciaDTO cdto = new ContactoEmergenciaDTO();
                    BeanUtils.copyProperties(c, cdto);
                    return cdto;
                }).collect(Collectors.toList()));

        dto.setAntecedentes(antecedenteRepository.findByIdPaciente(p.getIdPaciente()).stream()
                .map(a -> {
                    AntecedenteDTO adto = new AntecedenteDTO();
                    BeanUtils.copyProperties(a, adto);
                    return adto;
                }).collect(Collectors.toList()));

        return dto;
    }
}

