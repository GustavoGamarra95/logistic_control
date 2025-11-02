package com.logistic.control.entity;

import com.logistic.control.enums.EstadoInventario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad Inventario/Depósito
 * Módulo 4: Inventario
 */
@Entity
@Table(name = "inventario", indexes = {
    @Index(name = "idx_inventario_cliente", columnList = "cliente_id"),
    @Index(name = "idx_inventario_producto", columnList = "producto_id"),
    @Index(name = "idx_inventario_estado", columnList = "estado"),
    @Index(name = "idx_inventario_ubicacion", columnList = "ubicacion_deposito")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventario extends BaseEntity {

    @NotNull(message = "Cliente es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id")
    private Container container;

    @NotNull(message = "Producto es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(name = "ubicacion_deposito", length = 100)
    private String ubicacionDeposito;

    @Column(name = "zona", length = 50)
    private String zona;

    @Column(name = "pasillo", length = 20)
    private String pasillo;

    @Column(name = "rack", length = 20)
    private String rack;

    @Column(name = "nivel", length = 20)
    private String nivel;

    @NotNull(message = "Cantidad es requerida")
    @Min(value = 0, message = "Cantidad debe ser mayor o igual a 0")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "cantidad_reservada")
    @Builder.Default
    private Integer cantidadReservada = 0;

    @Column(name = "cantidad_disponible")
    private Integer cantidadDisponible;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoInventario estado = EstadoInventario.EN_TRANSITO;

    @Column(name = "fecha_entrada")
    private LocalDateTime fechaEntrada;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(name = "lote", length = 100)
    private String lote;

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    @Column(name = "numero_declaracion_aduanal", length = 100)
    private String numeroDeclaracionAduanal;

    @Column(name = "fecha_despacho_aduana")
    private LocalDateTime fechaDespachoAduana;

    @Column(name = "dias_almacenaje")
    private Integer diasAlmacenaje;

    @Column(name = "costo_almacenaje_diario")
    private Double costoAlmacenajeDiario;

    @Column(name = "costo_almacenaje_total")
    private Double costoAlmacenajeTotal;

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    // Helper methods
    public void entrada() {
        this.fechaEntrada = LocalDateTime.now();
        this.estado = EstadoInventario.EN_DEPOSITO;
        calcularCantidadDisponible();
    }

    public void salida(Integer cantidadSalida) {
        if (cantidadSalida > this.cantidadDisponible) {
            throw new IllegalArgumentException("Cantidad de salida mayor que cantidad disponible");
        }
        this.cantidad -= cantidadSalida;
        calcularCantidadDisponible();
        if (this.cantidad == 0) {
            this.fechaSalida = LocalDateTime.now();
            this.estado = EstadoInventario.DESPACHADO;
        }
    }

    public void reservar(Integer cantidadReservar) {
        if (cantidadReservar > this.cantidadDisponible) {
            throw new IllegalArgumentException("Cantidad a reservar mayor que cantidad disponible");
        }
        this.cantidadReservada += cantidadReservar;
        calcularCantidadDisponible();
        this.estado = EstadoInventario.RESERVADO;
    }

    public void liberarReserva(Integer cantidadLiberar) {
        if (cantidadLiberar > this.cantidadReservada) {
            throw new IllegalArgumentException("Cantidad a liberar mayor que cantidad reservada");
        }
        this.cantidadReservada -= cantidadLiberar;
        calcularCantidadDisponible();
        if (this.cantidadReservada == 0) {
            this.estado = EstadoInventario.DISPONIBLE;
        }
    }

    public void calcularCantidadDisponible() {
        this.cantidadDisponible = this.cantidad - this.cantidadReservada;
    }

    public void calcularDiasAlmacenaje() {
        if (this.fechaEntrada != null) {
            this.diasAlmacenaje = (int) java.time.Duration.between(
                this.fechaEntrada,
                this.fechaSalida != null ? this.fechaSalida : LocalDateTime.now()
            ).toDays();
        }
    }

    public void calcularCostoAlmacenaje() {
        calcularDiasAlmacenaje();
        if (this.diasAlmacenaje != null && this.costoAlmacenajeDiario != null) {
            this.costoAlmacenajeTotal = this.diasAlmacenaje * this.costoAlmacenajeDiario;
        }
    }

    @PrePersist
    @PreUpdate
    protected void onCreateOrUpdate() {
        calcularCantidadDisponible();
    }
}
