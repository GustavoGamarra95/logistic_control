package com.logistic.control.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Detalle/Ítem de una devolución.
 */
@Entity
@Table(name = "detalle_devolucion", indexes = {
    @Index(name = "idx_detalle_devolucion_devolucion", columnList = "devolucion_id"),
    @Index(name = "idx_detalle_devolucion_producto", columnList = "producto_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleDevolucion extends BaseEntity {

    @NotNull(message = "Devolución es requerida")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devolucion_id", nullable = false)
    private DevolucionVenta devolucion;

    @NotNull(message = "Producto es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_factura_id")
    private DetalleFactura detalleFactura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_pedido_id")
    private DetallePedido detallePedido;

    @NotNull(message = "Cantidad es requerida")
    @Min(value = 1, message = "Cantidad debe ser mayor a 0")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", precision = 15, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "descuento", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "porcentaje_iva")
    private Integer porcentajeIva;

    @Column(name = "monto_iva", precision = 15, scale = 2)
    private BigDecimal montoIva;

    @Column(name = "total", precision = 15, scale = 2)
    private BigDecimal total;

    @Column(name = "estado_producto", length = 50)
    private String estadoProducto; // BUENO, DANIADO, DEFECTUOSO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventario_entrada_id")
    private Inventario inventarioEntrada;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Calcular totales del detalle.
     */
    public void calcular() {
        if (precioUnitario == null) {
            precioUnitario = BigDecimal.ZERO;
        }

        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        this.subtotal = this.subtotal.subtract(descuento != null ? descuento : BigDecimal.ZERO);

        if (porcentajeIva != null && porcentajeIva > 0) {
            this.montoIva = this.subtotal.multiply(BigDecimal.valueOf(porcentajeIva))
                                         .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        } else {
            this.montoIva = BigDecimal.ZERO;
        }

        this.total = this.subtotal.add(this.montoIva);
    }

    @PrePersist
    @PreUpdate
    protected void onCreateOrUpdate() {
        calcular();
    }
}
