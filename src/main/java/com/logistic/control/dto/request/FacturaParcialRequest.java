package com.logistic.control.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Request para crear factura parcial desde pedido.
 * Permite facturar cantidades específicas de cada ítem del pedido.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaParcialRequest {

    @NotNull(message = "Pedido es requerido")
    private Long pedidoId;

    @NotBlank(message = "Tipo de factura es requerido (CONTADO o CREDITO)")
    private String tipo;

    private LocalDate fechaEmision;

    private LocalDate fechaVencimiento;

    private String condicionPago;

    private String observaciones;

    @NotEmpty(message = "Debe especificar al menos un ítem a facturar")
    @Valid
    private List<ItemFacturaParcialRequest> items;

    /**
     * Representa un ítem individual a facturar parcialmente.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemFacturaParcialRequest {

        @NotNull(message = "DetallePedido es requerido")
        private Long detallePedidoId;

        @NotNull(message = "Cantidad a facturar es requerida")
        @Min(value = 1, message = "Cantidad debe ser mayor a 0")
        private Integer cantidadAFacturar;

        /**
         * Permite sobrescribir el precio unitario del detalle pedido.
         * Útil para descuentos o ajustes especiales.
         * Si es null, se usa el precio del detalle pedido original.
         */
        private Double precioUnitarioOverride;
    }
}
