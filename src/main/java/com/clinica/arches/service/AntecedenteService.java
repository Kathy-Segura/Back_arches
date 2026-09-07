package com.clinica.arches.service;

import com.clinica.arches.dto.AntecedenteDTO;
import com.clinica.arches.model.AntecedentePaciente;
import com.clinica.arches.repository.AntecedentePacienteRepository;
import com.clinica.arches.repository.PacienteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AntecedenteService {

    private final AntecedentePacienteRepository antecedenteRepository;
    private final PacienteRepository pacienteRepository;

    public AntecedenteService(AntecedentePacienteRepository antecedenteRepository,
                              PacienteRepository pacienteRepository) {
        this.antecedenteRepository = antecedenteRepository;
        this.pacienteRepository = pacienteRepository;
    }

    // Sustenta la pestaña "Antecedentes" dentro de la vista "Ver" del paciente
    public List<AntecedenteDTO> listarPorPaciente(Integer idPaciente) {
        validarPacienteExiste(idPaciente);
        return antecedenteRepository.findByIdPaciente(idPaciente).stream()
                .map(this::convertir)
                .collect(Collectors.toList());
    }

    // Sustenta agregar un antecedente desde la vista "Editar" del paciente
    public AntecedenteDTO crear(Integer idPaciente, AntecedentePaciente antecedente) {
        validarPacienteExiste(idPaciente);
        antecedente.setIdAntecedente(null);
        antecedente.setIdPaciente(idPaciente);
        antecedente.setFechaRegistro(LocalDateTime.now());
        return convertir(antecedenteRepository.save(antecedente));
    }

    // Sustenta editar un antecedente existente desde la vista "Editar" del paciente
    public AntecedenteDTO actualizar(Integer idPaciente, Integer idAntecedente, AntecedentePaciente datos) {
        AntecedentePaciente existente = obtenerYValidarPertenencia(idPaciente, idAntecedente);
        BeanUtils.copyProperties(datos, existente, "idAntecedente", "idPaciente", "fechaRegistro");
        return convertir(antecedenteRepository.save(existente));
    }

    // Sustenta eliminar un antecedente desde la vista "Editar" del paciente
    public void eliminar(Integer idPaciente, Integer idAntecedente) {
        AntecedentePaciente existente = obtenerYValidarPertenencia(idPaciente, idAntecedente);
        antecedenteRepository.delete(existente);
    }

    private AntecedentePaciente obtenerYValidarPertenencia(Integer idPaciente, Integer idAntecedente) {
        AntecedentePaciente antecedente = antecedenteRepository.findById(idAntecedente)
                .orElseThrow(() -> new RuntimeException("Antecedente no encontrado: " + idAntecedente));
        if (!antecedente.getIdPaciente().equals(idPaciente)) {
            throw new RuntimeException("El antecedente " + idAntecedente + " no pertenece al paciente " + idPaciente);
        }
        return antecedente;
    }

    private void validarPacienteExiste(Integer idPaciente) {
        if (!pacienteRepository.existsById(idPaciente)) {
            throw new RuntimeException("Paciente no encontrado: " + idPaciente);
        }
    }

    private AntecedenteDTO convertir(AntecedentePaciente a) {
        AntecedenteDTO dto = new AntecedenteDTO();
        BeanUtils.copyProperties(a, dto);
        return dto;
    }
}
