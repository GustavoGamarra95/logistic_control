package com.logistic.control.entity;

import com.logistic.control.enums.EstadoDevolucion;
import com.logistic.control.enums.TipoDevolucion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una devolución de venta.
 * Soporta 3 tipos: devolución física, corrección de factura, ajuste de pedido.
 */
@Entity
@Table(name = "devoluciones_venta", indexes = {
    @Index(name = "idx_devolucion_estado", columnList = "estado"),
    @Index(name = "idx_devolucion_tipo", columnList = "tipo"),
    @Index(name = "idx_devolucion_cliente", columnList = "cliente_id"),
    @Index(name = "idx_devolucion_fecha", columnList = "fecha_solicitud")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevolucionVenta extends BaseEntity {

    @Column(name = "numero_devolucion", unique = true, length = 50)
    private String numeroDevolucion;

    @NotNull(message = "Tipo de devolución es requerido")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private TipoDevolucion tipo;

    @NotNull(message = "Estado es requerido")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 50)
    @Builder.Default
    private EstadoDevolucion estado = EstadoDevolucion.SOLICITADA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id")
    private Factura factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @NotNull(message = "Cliente es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "generar_nota_credito")
    @Builder.Default
    private Boolean generarNotaCredito = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_credito_id")
    private Factura notaCredito;

    @NotNull(message = "Fecha de solicitud es requerida")
    @Column(name = "fecha_solicitud", nullable = false)
    @Builder.Default
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(name = "fecha_completada")
    private LocalDateTime fechaCompletada;

    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "total_iva", precision = 15, scale = 2)
    private BigDecimal totalIva;

    @Column(name = "total", precision = 15, scale = 2)
    private BigDecimal total;

    @NotBlank(message = "Motivo es requerido")
    @Column(name = "motivo", nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobado_por_usuario_id")
    private Usuario aprobadoPor;

    @OneToMany(mappedBy = "devolucion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleDevolucion> detalles = new ArrayList<>();

    /**
     * Aprobar la devolución.
     */
    public void aprobar(Usuario usuario) {
        if (this.estado != EstadoDevolucion.SOLICITADA && this.estado != EstadoDevolucion.EN_REVISION) {
            throw new IllegalStateException(
                String.format("No se puede aprobar una devolución en estado %s", this.estado)
            );
        }
        this.estado = EstadoDevolucion.APROBADA;
        this.aprobadoPor = usuario;
        this.fechaAprobacion = LocalDateTime.now();
    }

    /**
     * Rechazar la devolución.
     */
    public void rechazar(Usuario usuario, String motivo) {
        if (this.estado != EstadoDevolucion.SOLICITADA && this.estado != EstadoDevolucion.EN_REVISION) {
            throw new IllegalStateException(
                String.format("No se puede rechazar una devolución en estado %s", this.estado)
            );
        }
        this.estado = EstadoDevolucion.RECHAZADA;
        this.aprobadoPor = usuario;
        this.fechaAprobacion = LocalDateTime.now();
        this.observaciones = (this.observaciones != null ? this.observaciones + "\n" : "")
                           + "RECHAZADA: " + motivo;
    }

    /**
     * Cambiar estado a EN_PROCESO.
     */
    public void iniciarProceso() {
        if (this.estado != EstadoDevolucion.APROBADA) {
            throw new IllegalStateException("Solo se pueden procesar devoluciones aprobadas");
        }
        this.estado = EstadoDevolucion.EN_PROCESO;
    }

    /**
     * Completar la devolución.
     */
    public void completar() {
        if (this.estado != EstadoDevolucion.EN_PROCESO && this.estado != EstadoDevolucion.APROBADA) {
            throw new IllegalStateException(
                String.format("No se puede completar una devolución en estado %s", this.estado)
            );
        }
        this.estado = EstadoDevolucion.COMPLETADA;
        this.fechaCompletada = LocalDateTime.now();
    }

    /**
     * Cancelar la devolución.
     */
    public void cancelar(String motivo) {
        if (this.estado == EstadoDevolucion.COMPLETADA) {
            throw new IllegalStateException("No se puede cancelar una devolución completada");
        }
        this.estado = EstadoDevolucion.CANCELADA;
        this.observaciones = (this.observaciones != null ? this.observaciones + "\n" : "")
                           + "CANCELADA: " + motivo;
    }

    /**
     * Calcular totales a partir de los detalles.
     */
    public void calcularTotales() {
        this.subtotal = detalles.stream()
            .map(DetalleDevolucion::getSubtotal)
            .filter(s -> s != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalIva = detalles.stream()
            .map(DetalleDevolucion::getMontoIva)
            .filter(i -> i != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.total = this.subtotal.add(this.totalIva);
    }

    /**
     * Validar que la devolución tenga los datos mínimos requeridos.
     */
    public void validar() {
        if (tipo == TipoDevolucion.PRODUCTO_FISICO || tipo == TipoDevolucion.CORRECCION_FACTURA) {
            if (factura == null) {
                throw new IllegalStateException(
                    String.format("Devolución de tipo %s requiere una factura asociada", tipo)
                );
            }
        }

        if (tipo == TipoDevolucion.AJUSTE_PEDIDO) {
            if (pedido == null) {
                throw new IllegalStateException("Devolución de tipo AJUSTE_PEDIDO requiere un pedido asociado");
            }
        }

        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalStateException("La devolución debe tener al menos un detalle");
        }
    }
}
