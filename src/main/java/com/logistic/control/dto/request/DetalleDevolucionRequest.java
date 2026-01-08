package com.logistic.control.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para un detalle de devolución.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleDevolucionRequest {

    @NotNull(message = "Producto es requerido")
    private Long productoId;

    private Long detalleFacturaId; // Para devoluciones de tipo PRODUCTO_FISICO o CORRECCION_FACTURA

    private Long detallePedidoId; // Para devoluciones de tipo AJUSTE_PEDIDO

    @NotNull(message = "Cantidad es requerida")
    @Min(value = 1, message = "Cantidad debe ser mayor a 0")
    private Integer cantidad;

    private Double precioUnitario;

    private Integer porcentajeIva;

    private String estadoProducto; // BUENO, DANIADO, DEFECTUOSO

    private String observaciones;
}
