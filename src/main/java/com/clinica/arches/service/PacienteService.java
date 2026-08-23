package com.clinica.arches.service;

import com.clinica.arches.dto.AntecedenteDTO;
import com.clinica.arches.dto.ContactoEmergenciaDTO;
import com.clinica.arches.dto.PacienteDTO;
import com.clinica.arches.model.Paciente;
import com.clinica.arches.repository.AntecedentePacienteRepository;
import com.clinica.arches.repository.ContactoEmergenciaRepository;
import com.clinica.arches.repository.PacienteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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

    // Listado liviano: sin contactos ni antecedentes, para no sobrecargar la tabla
    public List<PacienteDTO> listarTodos() {
        return pacienteRepository.findAll().stream()
                .map(this::convertirBasico)
                .collect(Collectors.toList());
    }

    // Detalle completo: incluye contactos y antecedentes
    public PacienteDTO buscarPorId(Integer id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado: " + id));
        return convertirCompleto(paciente);
    }

    public PacienteDTO crear(Paciente paciente) {
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
        dto.setEdad(Period.between(p.getFechaNacimiento(), java.time.LocalDate.now()).getYears());
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
