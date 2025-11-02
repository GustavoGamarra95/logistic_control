package com.logistic.control.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Factura de Proveedor (Cuentas por Pagar)
 */
@Entity
@Table(name = "facturas_proveedor", indexes = {
    @Index(name = "idx_factura_prov_proveedor", columnList = "proveedor_id"),
    @Index(name = "idx_factura_prov_fecha", columnList = "fecha_emision"),
    @Index(name = "idx_factura_prov_estado", columnList = "pagada")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaProveedor extends BaseEntity {

    @NotNull(message = "Proveedor es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @Column(name = "numero_factura", length = 50)
    private String numeroFactura;

    @NotNull(message = "Fecha de emisión es requerida")
    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @NotNull(message = "Monto es requerido")
    @Column(name = "monto", nullable = false)
    private Double monto;

    @Column(name = "iva")
    private Double iva;

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "moneda", length = 10)
    @Builder.Default
    private String moneda = "PYG";

    @Column(name = "pagada")
    @Builder.Default
    private Boolean pagada = false;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "referencia_pago", length = 100)
    private String referenciaPago;

    @Column(name = "concepto", length = 500)
    private String concepto;

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    // Helper methods
    public void registrarPago(LocalDateTime fechaPago, String metodoPago, String referencia) {
        this.pagada = true;
        this.fechaPago = fechaPago;
        this.metodoPago = metodoPago;
        this.referenciaPago = referencia;
    }

    @PrePersist
    protected void onCreate() {
        if (this.fechaEmision == null) {
            this.fechaEmision = LocalDateTime.now();
        }
        if (this.total == null && this.monto != null) {
            this.total = this.monto + (this.iva != null ? this.iva : 0);
        }
    }
}
