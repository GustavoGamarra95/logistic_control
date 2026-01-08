package com.logistic.control.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entidad que representa el detalle de un pedido
 */
@Entity
@Table(name = "detalle_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class DetallePedido extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subTotal;

    /**
     * Cantidad total facturada de este detalle de pedido.
     * Suma de todas las facturas activas asociadas.
     */
    @Column(name = "cantidad_facturada", nullable = false)
    @Builder.Default
    private Integer cantidadFacturada = 0;

    /**
     * Cantidad pendiente de facturar (columna generada en DB).
     * Valor calculado: cantidad - cantidad_facturada
     */
    @Column(name = "cantidad_pendiente", insertable = false, updatable = false)
    private Integer cantidadPendiente;

    /**
     * Registra facturación de una cantidad específica.
     *
     * @param cantidadAFacturar Cantidad a facturar
     * @throws IllegalArgumentException si la cantidad excede la cantidad pendiente
     */
    public void facturar(Integer cantidadAFacturar) {
        if (cantidadAFacturar == null || cantidadAFacturar <= 0) {
            throw new IllegalArgumentException("Cantidad a facturar debe ser mayor a 0");
        }

        Integer pendiente = this.cantidad - this.cantidadFacturada;
        if (cantidadAFacturar > pendiente) {
            throw new IllegalArgumentException(
                String.format("Cantidad a facturar (%d) excede cantidad pendiente (%d)",
                             cantidadAFacturar, pendiente)
            );
        }

        this.cantidadFacturada += cantidadAFacturar;
    }

    /**
     * Revierte facturación de una cantidad (por ejemplo, por anulación de factura).
     *
     * @param cantidadARevertir Cantidad a revertir
     * @throws IllegalArgumentException si la cantidad excede la cantidad facturada
     */
    public void revertirFacturacion(Integer cantidadARevertir) {
        if (cantidadARevertir == null || cantidadARevertir <= 0) {
            throw new IllegalArgumentException("Cantidad a revertir debe ser mayor a 0");
        }

        if (cantidadARevertir > this.cantidadFacturada) {
            throw new IllegalArgumentException(
                String.format("Cantidad a revertir (%d) excede cantidad facturada (%d)",
                             cantidadARevertir, this.cantidadFacturada)
            );
        }

        this.cantidadFacturada -= cantidadARevertir;
    }

    /**
     * Verifica si el detalle está completamente facturado.
     *
     * @return true si toda la cantidad fue facturada
     */
    public boolean estaCompletamenteFacturado() {
        return this.cantidadFacturada != null &&
               this.cantidadFacturada.equals(this.cantidad);
    }

    /**
     * Obtiene la cantidad pendiente de facturar.
     *
     * @return cantidad pendiente (cantidad - cantidadFacturada)
     */
    public Integer getCantidadPendienteFacturar() {
        if (this.cantidadPendiente != null) {
            return this.cantidadPendiente; // Usar computed column si existe
        }
        return this.cantidad - (this.cantidadFacturada != null ? this.cantidadFacturada : 0);
    }

    /**
     * Calcula el porcentaje de facturación del detalle.
     *
     * @return porcentaje facturado (0-100)
     */
    public Double getPorcentajeFacturado() {
        if (this.cantidad == null || this.cantidad == 0) {
            return 0.0;
        }
        Integer facturado = this.cantidadFacturada != null ? this.cantidadFacturada : 0;
        return (facturado.doubleValue() / this.cantidad) * 100.0;
    }
}
