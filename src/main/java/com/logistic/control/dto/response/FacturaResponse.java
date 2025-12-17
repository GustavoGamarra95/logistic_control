package com.logistic.control.dto.response;

import com.logistic.control.enums.EstadoFactura;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponse {

    private Long id;
    private String numeroFactura;
    private LocalDateTime fechaEmision;
    private LocalDate fechaVencimiento;
    private Long clienteId;
    private String clienteNombre;
    private String clienteRuc;
    private Double subtotal;
    private Double iva5;
    private Double iva10;
    private Double totalIva;
    private Double total;
    private Double descuento;
    private String moneda;
    private EstadoFactura estado;
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
    private Double pagado;
    private String observaciones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
