package com.clinica.arches.service;

import com.clinica.arches.dto.PersonalDTO;
import com.clinica.arches.model.Personal;
import com.clinica.arches.repository.PersonalRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonalService {

    private final PersonalRepository personalRepository;

    public PersonalService(PersonalRepository personalRepository) {
        this.personalRepository = personalRepository;
    }

    /** Sustenta el selector de "Odontólogo" en el módulo de citas (calendario, listado y formulario). */
    public List<PersonalDTO> listarOdontologosActivos() {
        return personalRepository.findByCargoAndEstadoOrderByNombreCompletoAsc("Odontólogo", "activo").stream()
                .map(this::convertir)
                .collect(Collectors.toList());
    }

    private PersonalDTO convertir(Personal p) {
        PersonalDTO dto = new PersonalDTO();
        BeanUtils.copyProperties(p, dto);
        return dto;
    }
}
