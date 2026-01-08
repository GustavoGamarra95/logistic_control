package com.logistic.control.dto.request;

import com.logistic.control.enums.TipoDevolucion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request para crear una devolución.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionRequest {

    @NotNull(message = "Tipo de devolución es requerido")
    private TipoDevolucion tipo;

    @NotNull(message = "Cliente es requerido")
    private Long clienteId;

    private Long facturaId; // Requerido para PRODUCTO_FISICO y CORRECCION_FACTURA

    private Long pedidoId; // Requerido para AJUSTE_PEDIDO

    @NotBlank(message = "Motivo es requerido")
    private String motivo;

    private String observaciones;

    private Boolean generarNotaCredito;

    @NotEmpty(message = "Debe especificar al menos un ítem a devolver")
    @Valid
    private List<DetalleDevolucionRequest> detalles;
}
