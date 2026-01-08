package com.logistic.control.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response de un detalle de devolución.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleDevolucionResponse {

    private Long id;
    private Long productoId;
    private String productoDescripcion;
    private String productoCodigo;
    private Long detalleFacturaId;
    private Long detallePedidoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal subtotal;
    private Integer porcentajeIva;
    private BigDecimal montoIva;
    private BigDecimal total;
    private String estadoProducto;
    private Long inventarioEntradaId;
    private String observaciones;
}
