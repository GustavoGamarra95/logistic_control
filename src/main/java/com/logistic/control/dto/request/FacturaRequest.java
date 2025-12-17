package com.logistic.control.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaRequest {

    @NotNull(message = "Cliente es requerido")
    private Long clienteId;

    private Long pedidoId;

    @NotNull(message = "Subtotal es requerido")
    private Double subtotal;

    private Double descuento;

    private String moneda;

    private String timbrado;

    private String establecimiento;

    private String puntoExpedicion;

    private String observaciones;
}
