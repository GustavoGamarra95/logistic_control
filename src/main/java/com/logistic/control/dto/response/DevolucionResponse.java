package com.logistic.control.dto.response;

import com.logistic.control.enums.EstadoDevolucion;
import com.logistic.control.enums.TipoDevolucion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response de una devolución.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionResponse {

    private Long id;
    private String numeroDevolucion;
    private TipoDevolucion tipo;
    private EstadoDevolucion estado;
    private Long facturaId;
    private String numeroFactura;
    private Long pedidoId;
    private String codigoTracking;
    private Long clienteId;
    private String clienteNombre;
    private Boolean generarNotaCredito;
    private Long notaCreditoId;
    private String numeroNotaCredito;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaAprobacion;
    private LocalDateTime fechaCompletada;
    private BigDecimal subtotal;
    private BigDecimal totalIva;
    private BigDecimal total;
    private String motivo;
    private String observaciones;
    private Long aprobadoPorId;
    private String aprobadoPorNombre;
    private List<DetalleDevolucionResponse> detalles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
