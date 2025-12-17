package com.logistic.control.dto.request;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Request para generar documento electrónico SIFEN
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SifenDocumentoRequest {

    /**
     * ID de la factura a enviar a SIFEN
     */
    private Long facturaId;

    /**
     * Tipo de documento electrónico
     * 1 = Factura Electrónica
     * 4 = Autofactura Electrónica
     * 5 = Nota de Crédito Electrónica
     * 6 = Nota de Débito Electrónica
     * 7 = Nota de Remisión Electrónica
     */
    private Integer tipoDocumento;

    /**
     * Tipo de emisión
     * 1 = Normal
     * 2 = Contingencia
     */
    private Integer tipoEmision;

    /**
     * Fecha y hora de emisión
     */
    private LocalDateTime fechaEmision;

    /**
     * Tipo de transacción
     * 1 = Venta de mercaderías
     * 2 = Prestación de servicios
     * 3 = Mixto (Mercaderías y Servicios)
     */
    private Integer tipoTransaccion;

    /**
     * Tipo de impuesto
     * 1 = IVA
     */
    private Integer tipoImpuesto;

    /**
     * Moneda
     * PYG = Guaraníes
     * USD = Dólares
     */
    private String moneda;

    /**
     * Condición de la operación
     * 1 = Contado
     * 2 = Crédito
     */
    private Integer condicionOperacion;

    /**
     * Medio de pago
     * 01 = Efectivo
     * 02 = Cheque
     * 03 = Tarjeta de crédito
     * 04 = Tarjeta de débito
     * 05 = Transferencia
     * 99 = Otro
     */
    private String medioPago;

    /**
     * Observaciones
     */
    private String observaciones;
}
