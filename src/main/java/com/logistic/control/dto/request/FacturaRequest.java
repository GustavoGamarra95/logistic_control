package com.logistic.control.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaRequest {

    @NotNull(message = "Cliente es requerido")
    private Long clienteId;

    private Long pedidoId;

    @NotBlank(message = "Tipo de factura es requerido")
    private String tipo; // CONTADO o CREDITO

    private LocalDate fechaEmision;

    private LocalDate fechaVencimiento;

    private String condicionPago;

    @NotBlank(message = "Moneda es requerida")
    private String moneda;

    private String observaciones;

    @NotEmpty(message = "Debe incluir al menos un ítem")
    @Valid
    private List<ItemFacturaRequest> items;
}
