package com.logistic.control.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Detalle/Ítem de Factura
 */
@Entity
@Table(name = "detalle_factura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Factura es requerida")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    /**
     * Referencia al detalle de pedido original (para rastrear facturación parcial).
     * Permite saber de qué detalle de pedido proviene este ítem de factura.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detalle_pedido_id")
    private DetallePedido detallePedido;

    @Column(name = "codigo", length = 50)
    private String codigo;

    @NotBlank(message = "Descripción es requerida")
    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    @NotNull(message = "Cantidad es requerida")
    @Min(value = 1, message = "Cantidad debe ser mayor a 0")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "unidad_medida", length = 20)
    private String unidadMedida;

    @NotNull(message = "Precio unitario es requerido")
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    @Column(name = "descuento")
    @Builder.Default
    private Double descuento = 0.0;

    @Column(name = "subtotal")
    private Double subtotal;

    @Column(name = "porcentaje_iva")
    private Integer porcentajeIva; // 0, 5, 10

    @Column(name = "monto_iva")
    private Double montoIva;

    @Column(name = "total")
    private Double total;

    @Column(name = "codigo_ncm", length = 20)
    private String codigoNcm;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    // Helper methods
    public void calcular() {
        this.subtotal = this.cantidad * this.precioUnitario;
        this.subtotal -= this.descuento;

        if (this.porcentajeIva != null && this.porcentajeIva > 0) {
            this.montoIva = this.subtotal * (this.porcentajeIva / 100.0);
        } else {
            this.montoIva = 0.0;
        }

        this.total = this.subtotal + this.montoIva;
    }

    @PrePersist
    @PreUpdate
    protected void onCreateOrUpdate() {
        calcular();
    }
}
