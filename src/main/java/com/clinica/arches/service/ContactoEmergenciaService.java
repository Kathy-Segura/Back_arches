package com.clinica.arches.service;

import com.clinica.arches.dto.ContactoEmergenciaDTO;
import com.clinica.arches.model.ContactoEmergencia;
import com.clinica.arches.repository.ContactoEmergenciaRepository;
import com.clinica.arches.repository.PacienteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactoEmergenciaService {

    private final ContactoEmergenciaRepository contactoRepository;
    private final PacienteRepository pacienteRepository;

    public ContactoEmergenciaService(ContactoEmergenciaRepository contactoRepository,
                                     PacienteRepository pacienteRepository) {
        this.contactoRepository = contactoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    // Sustenta la pestaña "Contactos" dentro de la vista "Ver" del paciente
    public List<ContactoEmergenciaDTO> listarPorPaciente(Integer idPaciente) {
        validarPacienteExiste(idPaciente);
        return contactoRepository.findByIdPaciente(idPaciente).stream()
                .map(this::convertir)
                .collect(Collectors.toList());
    }

    // Sustenta agregar un contacto desde la vista "Editar" del paciente
    public ContactoEmergenciaDTO crear(Integer idPaciente, ContactoEmergencia contacto) {
        validarPacienteExiste(idPaciente);
        contacto.setIdContacto(null);
        contacto.setIdPaciente(idPaciente);
        return convertir(contactoRepository.save(contacto));
    }

    // Sustenta editar un contacto existente desde la vista "Editar" del paciente
    public ContactoEmergenciaDTO actualizar(Integer idPaciente, Integer idContacto, ContactoEmergencia datos) {
        ContactoEmergencia existente = obtenerYValidarPertenencia(idPaciente, idContacto);
        BeanUtils.copyProperties(datos, existente, "idContacto", "idPaciente");
        return convertir(contactoRepository.save(existente));
    }

    // Sustenta eliminar un contacto desde la vista "Editar" del paciente
    public void eliminar(Integer idPaciente, Integer idContacto) {
        ContactoEmergencia existente = obtenerYValidarPertenencia(idPaciente, idContacto);
        contactoRepository.delete(existente);
    }

    private ContactoEmergencia obtenerYValidarPertenencia(Integer idPaciente, Integer idContacto) {
        ContactoEmergencia contacto = contactoRepository.findById(idContacto)
                .orElseThrow(() -> new RuntimeException("Contacto no encontrado: " + idContacto));
        if (!contacto.getIdPaciente().equals(idPaciente)) {
            throw new RuntimeException("El contacto " + idContacto + " no pertenece al paciente " + idPaciente);
        }
        return contacto;
    }

    private void validarPacienteExiste(Integer idPaciente) {
        if (!pacienteRepository.existsById(idPaciente)) {
            throw new RuntimeException("Paciente no encontrado: " + idPaciente);
        }
    }

    private ContactoEmergenciaDTO convertir(ContactoEmergencia c) {
        ContactoEmergenciaDTO dto = new ContactoEmergenciaDTO();
        BeanUtils.copyProperties(c, dto);
        return dto;
    }
}
