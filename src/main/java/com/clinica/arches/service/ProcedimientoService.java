package com.clinica.arches.service;

import com.clinica.arches.dto.ProcedimientoDTO;
import com.clinica.arches.model.CatalogoProcedimiento;
import com.clinica.arches.repository.ProcedimientoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcedimientoService {

    private final ProcedimientoRepository procedimientoRepository;

    public ProcedimientoService(ProcedimientoRepository procedimientoRepository) {
        this.procedimientoRepository = procedimientoRepository;
    }

    /** Sustenta el selector de "Procedimiento" en el formulario de citas. */
    public List<ProcedimientoDTO> listarActivos() {
        return procedimientoRepository.findByEstadoOrderByNombreProcedimientoAsc("activo").stream()
                .map(this::convertir)
                .collect(Collectors.toList());
    }

    private ProcedimientoDTO convertir(CatalogoProcedimiento p) {
        ProcedimientoDTO dto = new ProcedimientoDTO();
        BeanUtils.copyProperties(p, dto);
        return dto;
    }
}
