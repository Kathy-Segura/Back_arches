package com.clinica.arches.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TratamientoRequestDTO {

    @NotNull(message = "El paciente es obligatorio")
    private Integer idPaciente;

    @NotNull(message = "El procedimiento es obligatorio")
    private Integer idProcedimiento;

    @NotNull(message = "El odontólogo es obligatorio")
    private Integer idPersonal;

    private LocalDate fechaProgramada;

    @NotNull(message = "El costo es obligatorio")
    private BigDecimal costoTotal;

    private Integer sesionesPlanificadas = 1;

    private String notas;

    /**
     * Solo se respetan en edición; en creación el service fuerza
     * estadoAvance = 'propuesto' y estadoPago = 'pendiente' sin importar
     * lo que venga aquí, igual que el patrón usado en Personal con "estado".
     */
    private String estadoAvance;
    private String estadoPago;
}
