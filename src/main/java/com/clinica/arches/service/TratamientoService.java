package com.clinica.arches.service;

import com.clinica.arches.dto.*;
import com.clinica.arches.model.Personal;
import com.clinica.arches.model.Tratamiento;
import com.clinica.arches.model.TratamientoSesion;
import com.clinica.arches.repository.PersonalRepository;
import com.clinica.arches.repository.TratamientoRepository;
import com.clinica.arches.repository.TratamientoSesionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TratamientoService {

    private static final String AVANCE_INICIAL = "propuesto";
    private static final String PAGO_INICIAL = "pendiente";

    private final TratamientoRepository tratamientoRepository;
    private final TratamientoSesionRepository sesionRepository;
    private final PersonalRepository personalRepository;
    private final EntityManager entityManager;

    public TratamientoService(
            TratamientoRepository tratamientoRepository,
            TratamientoSesionRepository sesionRepository,
            PersonalRepository personalRepository,
            EntityManager entityManager
    ) {
        this.tratamientoRepository = tratamientoRepository;
        this.sesionRepository = sesionRepository;
        this.personalRepository = personalRepository;
        this.entityManager = entityManager;
    }

    public PaginaDTO<TratamientoDTO> listar(Integer idPaciente, Integer idPersonal, String estadoAvance, String estadoPago, Pageable pageable) {
        var page = tratamientoRepository.buscar(idPaciente, idPersonal, blankToNull(estadoAvance), blankToNull(estadoPago), pageable);
        return PaginaDTO.desde(page.map(t -> convertir(t, false)));
    }

    public TratamientoDTO obtener(Integer id) {
        return convertir(buscarEntidad(id), true);
    }

    @Transactional
    public TratamientoDTO crear(TratamientoRequestDTO request) {
        Tratamiento tratamiento = new Tratamiento();
        aplicarCambios(tratamiento, request);
        tratamiento.setEstadoAvance(AVANCE_INICIAL);
        tratamiento.setEstadoPago(PAGO_INICIAL);
        return convertir(tratamientoRepository.save(tratamiento), false);
    }

    @Transactional
    public TratamientoDTO actualizar(Integer id, TratamientoRequestDTO request) {
        Tratamiento tratamiento = buscarEntidad(id);
        aplicarCambios(tratamiento, request);
        if (request.getEstadoAvance() != null && !request.getEstadoAvance().isBlank()) {
            tratamiento.setEstadoAvance(request.getEstadoAvance());
        }
        if (request.getEstadoPago() != null && !request.getEstadoPago().isBlank()) {
            tratamiento.setEstadoPago(request.getEstadoPago());
        }
        return convertir(tratamientoRepository.save(tratamiento), true);
    }

    /**
     * Registra una sesión de avance. clinica.tratamiento_sesiones tiene un
     * trigger (trg_sesiones_actualiza_avance) que recalcula el avance del
     * tratamiento directamente en la base de datos al insertar; como no
     * conozco su lógica exacta, después de guardar se hace
     * entityManager.refresh(...) para traer el estado real que dejó el
     * trigger, en vez de asumir cuál quedó desde el lado de Java.
     */
    @Transactional
    public TratamientoDTO registrarSesion(Integer idTratamiento, SesionRequestDTO request) {
        Tratamiento tratamiento = buscarEntidad(idTratamiento);

        TratamientoSesion sesion = new TratamientoSesion();
        sesion.setIdTratamiento(idTratamiento);
        sesion.setIdCita(request.getIdCita());
        sesion.setFechaSesion(request.getFechaSesion() != null ? request.getFechaSesion() : LocalDateTime.now());
        sesion.setDescripcionAvance(request.getDescripcionAvance());
        sesionRepository.save(sesion);

        entityManager.flush();
        entityManager.refresh(tratamiento); // recoge lo que haya cambiado el trigger

        return convertir(tratamiento, true);
    }

    private Tratamiento buscarEntidad(Integer id) {
        return tratamientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe tratamiento con id " + id));
    }

    private void aplicarCambios(Tratamiento tratamiento, TratamientoRequestDTO request) {
        tratamiento.setIdPaciente(request.getIdPaciente());
        tratamiento.setIdProcedimiento(request.getIdProcedimiento());
        tratamiento.setIdPersonal(request.getIdPersonal());
        tratamiento.setFechaProgramada(request.getFechaProgramada());
        tratamiento.setCostoTotal(request.getCostoTotal());
        tratamiento.setSesionesPlanificadas(
                request.getSesionesPlanificadas() != null ? request.getSesionesPlanificadas() : 1
        );
        tratamiento.setNotas(request.getNotas());
    }

    private TratamientoDTO convertir(Tratamiento t, boolean conSesiones) {
        TratamientoDTO dto = new TratamientoDTO();
        BeanUtils.copyProperties(t, dto);

        Optional<Personal> odontologo = personalRepository.findById(t.getIdPersonal());
        dto.setPersonalNombre(odontologo.map(Personal::getNombreCompleto).orElse(null));

        if (conSesiones) {
            List<SesionDTO> sesiones = sesionRepository.findByIdTratamientoOrderByFechaSesionAsc(t.getIdTratamiento())
                    .stream().map(this::convertirSesion).collect(Collectors.toList());
            dto.setSesiones(sesiones);
        }
        return dto;
    }

    private SesionDTO convertirSesion(TratamientoSesion s) {
        SesionDTO dto = new SesionDTO();
        BeanUtils.copyProperties(s, dto);
        return dto;
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
