package com.clinica.arches.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "gastos", schema = "clinica")
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gasto")
    private Integer id;

    private String concepto;

    @Column(nullable = false)
    private BigDecimal monto;

    /** Atributo Java "fecha" (el que usan las queries); columna real: fecha_gasto. */
    @Column(name = "fecha_gasto", nullable = false)
    private LocalDate fecha;

    public Gasto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}
