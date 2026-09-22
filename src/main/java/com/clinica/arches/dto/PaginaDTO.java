package com.clinica.arches.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Envoltorio simple de paginación para no exponer directamente el
 * Page<T> de Spring Data (que serializa metadata interna innecesaria)
 * en las respuestas del API.
 */
@Data
public class PaginaDTO<T> {
    private List<T> contenido;
    private int pagina;      // página actual, 0-indexed
    private int tamanoPagina;
    private long totalElementos;
    private int totalPaginas;

    public static <T> PaginaDTO<T> desde(Page<T> page) {
        PaginaDTO<T> dto = new PaginaDTO<>();
        dto.setContenido(page.getContent());
        dto.setPagina(page.getNumber());
        dto.setTamanoPagina(page.getSize());
        dto.setTotalElementos(page.getTotalElements());
        dto.setTotalPaginas(page.getTotalPages());
        return dto;
    }
}
