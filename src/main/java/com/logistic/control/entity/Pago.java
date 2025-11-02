package com.logistic.control.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad Pago
 */
@Entity
@Table(name = "pagos", indexes = {
    @Index(name = "idx_pago_factura", columnList = "factura_id"),
    @Index(name = "idx_pago_fecha", columnList = "fecha_pago")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Factura es requerida")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @NotNull(message = "Monto es requerido")
    @Positive(message = "Monto debe ser mayor a 0")
    @Column(name = "monto", nullable = false)
    private Double monto;

    @NotNull(message = "Fecha de pago es requerida")
    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago; // Efectivo, Transferencia, Tarjeta, etc.

    @Column(name = "referencia", length = 100)
    private String referencia; // Número de comprobante, transacción, etc.

    @Column(name = "banco", length = 100)
    private String banco;

    @Column(name = "numero_cuenta", length = 50)
    private String numeroCuenta;

    @Column(name = "moneda", length = 10)
    @Builder.Default
    private String moneda = "PYG";

    @Column(name = "tipo_cambio")
    private Double tipoCambio;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @PrePersist
    protected void onCreate() {
        if (this.fechaPago == null) {
            this.fechaPago = LocalDateTime.now();
        }
    }
}
