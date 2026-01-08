package com.logistic.control.dto.response;

import com.logistic.control.enums.EstadoFactura;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponse {

    private Long id;
    private String numeroFactura;
    private String numeroDocumento; // Número de documento para frontend
    private String tipo; // CONTADO o CREDITO
    private LocalDateTime fechaEmision;
    private LocalDate fechaVencimiento;
    private String condicionPago;
    private Long clienteId;
    private String clienteNombre;
    private String clienteRazonSocial; // Alias para clienteNombre
    private String clienteRuc;
    private Double subtotal;
    private Double iva5;
    private Double iva10;
    private Double totalIva;
    private Double ivaTotal; // Alias para totalIva (compatibilidad frontend)
    private Double total;
    private Double descuento;
    private String moneda;
    private EstadoFactura estado;
    private String estadoPago; // PENDIENTE, PARCIAL, PAGADO, VENCIDO
    private String cdc;
    private String timbrado;
    private String establecimiento;
    private String puntoExpedicion;
    private LocalDateTime fechaAprobacionSifen;
    private String codigoEstadoSifen;
    private String mensajeSifen;
    private String qrCode;
    private String urlKude;
    private Double saldo;
    private Double saldoPendiente; // Alias para saldo (compatibilidad frontend)
    private Double pagado;
    private String observaciones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Ítems de la factura
    private List<ItemFacturaResponse> items;
}
