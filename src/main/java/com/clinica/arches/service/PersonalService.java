package com.clinica.arches.service;

import com.clinica.arches.dto.PaginaDTO;
import com.clinica.arches.dto.PersonalDTO;
import com.clinica.arches.dto.PersonalDetalleDTO;
import com.clinica.arches.dto.PersonalRequestDTO;
import com.clinica.arches.model.Personal;
import com.clinica.arches.repository.PersonalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonalService {

    private static final String ESTADO_ACTIVO = "activo";
    private static final String ESTADO_INACTIVO = "inactivo";

    private final PersonalRepository personalRepository;

    public PersonalService(PersonalRepository personalRepository) {
        this.personalRepository = personalRepository;
    }

    /** Sustenta el selector de "Odontólogo" en el módulo de citas (calendario, listado y formulario). */
    public List<PersonalDTO> listarOdontologosActivos() {
        return personalRepository.findByCargoAndEstadoOrderByNombreCompletoAsc("Odontólogo", ESTADO_ACTIVO).stream()
                .map(this::convertir)
                .collect(Collectors.toList());
    }

    /** Listado paginado del módulo de Personal, con filtro opcional por cargo y búsqueda por nombre. */
    public PaginaDTO<PersonalDetalleDTO> listar(String cargo, String q, Pageable pageable) {
        String cargoFiltro = (cargo == null || cargo.isBlank()) ? null : cargo;
        String qFiltro = (q == null || q.isBlank()) ? null : q.trim();
        Page<Personal> page = personalRepository.buscar(cargoFiltro, qFiltro, pageable);
        return PaginaDTO.desde(page.map(this::convertirDetalle));
    }

    public PersonalDetalleDTO obtener(Integer id) {
        return convertirDetalle(buscarEntidad(id));
    }

    @Transactional
    public PersonalDetalleDTO crear(PersonalRequestDTO request) {
        Personal personal = new Personal();
        aplicarCambios(personal, request);
        personal.setEstado(ESTADO_ACTIVO); // toda alta nace activa, sin importar lo que venga en el request
        return convertirDetalle(personalRepository.save(personal));
    }

    @Transactional
    public PersonalDetalleDTO actualizar(Integer id, PersonalRequestDTO request) {
        Personal personal = buscarEntidad(id);
        aplicarCambios(personal, request);
        if (request.getEstado() != null && !request.getEstado().isBlank()) {
            personal.setEstado(request.getEstado());
        }
        return convertirDetalle(personalRepository.save(personal));
    }

    /**
     * "Eliminar" desde el módulo de Personal es una baja lógica (estado =
     * inactivo), no un borrado físico: el registro puede tener citas
     * asociadas y borrarlo rompería esa referencia. Si se necesita borrado
     * físico real, se puede agregar un endpoint aparte que valide primero
     * que no existan citas relacionadas.
     */
    @Transactional
    public void desactivar(Integer id) {
        Personal personal = buscarEntidad(id);
        personal.setEstado(ESTADO_INACTIVO);
        personalRepository.save(personal);
    }

    private Personal buscarEntidad(Integer id) {
        return personalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe personal con id " + id));
    }

    private void aplicarCambios(Personal personal, PersonalRequestDTO request) {
        personal.setNombreCompleto(request.getNombreCompleto());
        personal.setCargo(request.getCargo());
        personal.setTelefono(request.getTelefono());
        personal.setCorreo(request.getCorreo());
        personal.setHorarioTexto(request.getHorarioTexto());
        personal.setNumeroLicencia(request.getNumeroLicencia());
        personal.setFechaIngreso(request.getFechaIngreso());
        personal.setIdEspecialidad(request.getIdEspecialidad());
        personal.setIdUsuario(request.getIdUsuario());
    }

    private PersonalDTO convertir(Personal p) {
        PersonalDTO dto = new PersonalDTO();
        BeanUtils.copyProperties(p, dto);
        return dto;
    }

    private PersonalDetalleDTO convertirDetalle(Personal p) {
        PersonalDetalleDTO dto = new PersonalDetalleDTO();
        BeanUtils.copyProperties(p, dto);
        return dto;
    }
}
