package com.clinica.arches.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Tabla clinica.pagos_tratamiento. Cada pago pertenece a un tratamiento. */
@Entity
@Table(name = "pagos_tratamiento", schema = "clinica")
@Data
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    /** FK a clinica.tratamientos; Integer simple, igual que en Tratamiento. */
    @Column(name = "id_tratamiento", nullable = false)
    private Integer idTratamiento;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "referencia")
    private String referencia;
}
