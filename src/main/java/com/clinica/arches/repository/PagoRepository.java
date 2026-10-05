package com.clinica.arches.repository;

import com.clinica.arches.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Integer> {

    /** Rango [desde, hasta): hasta es exclusivo. Devuelve null si no hay pagos. */
    @Query("select sum(p.monto) from Pago p where p.fechaPago >= :desde and p.fechaPago < :hasta")
    BigDecimal totalEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("""
            select year(p.fechaPago) as anio, month(p.fechaPago) as mes, sum(p.monto) as total
            from Pago p
            where p.fechaPago >= :desde and p.fechaPago < :hasta
            group by year(p.fechaPago), month(p.fechaPago)
            """)
    List<MesMontoRow> totalPorMes(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
