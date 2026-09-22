package com.clinica.arches.service;

import com.clinica.arches.dto.PaginaDTO;
import com.clinica.arches.dto.ProcedimientoDTO;
import com.clinica.arches.dto.ProcedimientoRequestDTO;
import com.clinica.arches.model.CatalogoProcedimiento;
import com.clinica.arches.repository.ProcedimientoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class ProcedimientoService {

    private final ProcedimientoRepository procedimientoRepository;

    public ProcedimientoService(ProcedimientoRepository procedimientoRepository) {
        this.procedimientoRepository = procedimientoRepository;
    }

    /** Sustenta el selector de "Procedimiento" en el formulario de citas. No se tocó. */
    public List<ProcedimientoDTO> listarActivos() {
        return procedimientoRepository.findByEstadoOrderByNombreProcedimientoAsc("activo").stream()
                .map(this::convertir)
                .collect(Collectors.toList());
    }

    /** Listado paginado para el módulo administrativo de Procedimientos. */
    public PaginaDTO<ProcedimientoDTO> listar(String categoria, String q, Pageable pageable) {
        String categoriaFiltro = (categoria == null || categoria.isBlank()) ? null : categoria;
        String qFiltro = (q == null || q.isBlank()) ? null : q.trim();
        Page<CatalogoProcedimiento> page = procedimientoRepository.buscar(categoriaFiltro, qFiltro, pageable);
        return PaginaDTO.desde(page.map(this::convertir));
    }

    public ProcedimientoDTO obtener(Integer id) {
        return convertir(buscarOFallar(id));
    }

    public ProcedimientoDTO crear(ProcedimientoRequestDTO datos) {
        CatalogoProcedimiento p = new CatalogoProcedimiento();
        aplicarDatos(p, datos);
        p.setEstado("activo");
        return convertir(procedimientoRepository.save(p));
    }

    public ProcedimientoDTO actualizar(Integer id, ProcedimientoRequestDTO datos) {
        CatalogoProcedimiento p = buscarOFallar(id);
        aplicarDatos(p, datos);
        return convertir(procedimientoRepository.save(p));
    }

    /** Baja lógica: no se borra el registro, solo se marca como inactivo. */
    public void desactivar(Integer id) {
        CatalogoProcedimiento p = buscarOFallar(id);
        p.setEstado("inactivo");
        procedimientoRepository.save(p);
    }

    private CatalogoProcedimiento buscarOFallar(Integer id) {
        return procedimientoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Procedimiento no encontrado"));
    }

    private void aplicarDatos(CatalogoProcedimiento p, ProcedimientoRequestDTO datos) {
        p.setNombreProcedimiento(datos.getNombreProcedimiento());
        p.setCategoria(datos.getCategoria());
        p.setDuracionMinutos(datos.getDuracionMinutos());
        p.setPrecio(datos.getPrecio());
        p.setDescripcion(datos.getDescripcion());
    }

    private ProcedimientoDTO convertir(CatalogoProcedimiento p) {
        ProcedimientoDTO dto = new ProcedimientoDTO();
        BeanUtils.copyProperties(p, dto);
        return dto;
    }
}
