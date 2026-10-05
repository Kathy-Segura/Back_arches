package com.clinica.arches.repository;

import com.clinica.arches.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    /** Devuelve null si no hay registros en el rango. */
    @Query("select sum(g.monto) from Gasto g where g.fecha >= :desde and g.fecha <= :hasta")
    BigDecimal totalEntre(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("""
            select year(g.fecha) as anio, month(g.fecha) as mes, sum(g.monto) as total
            from Gasto g
            where g.fecha >= :desde and g.fecha <= :hasta
            group by year(g.fecha), month(g.fecha)
            """)
    List<MesMontoRow> totalPorMes(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}
