package com.logistic.control.entity;

import com.logistic.control.enums.EstadoPedido;
import com.logistic.control.enums.TipoCarga;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Pedido/Envío
 * Módulo 2: Gestión de Pedidos
 */
@Entity
@Table(name = "pedidos", indexes = {
    @Index(name = "idx_pedido_tracking", columnList = "codigo_tracking"),
    @Index(name = "idx_pedido_estado", columnList = "estado"),
    @Index(name = "idx_pedido_fecha", columnList = "fecha_registro")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido extends BaseEntity {

    @NotNull(message = "Cliente es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotNull(message = "Fecha de registro es requerida")
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_carga")
    private TipoCarga tipoCarga;

    @NotBlank(message = "País de origen es requerido")
    @Column(name = "pais_origen", nullable = false, length = 100)
    private String paisOrigen;

    @NotBlank(message = "País de destino es requerido")
    @Column(name = "pais_destino", nullable = false, length = 100)
    private String paisDestino;

    @Column(name = "ciudad_origen", length = 100)
    private String ciudadOrigen;

    @Column(name = "ciudad_destino", length = 100)
    private String ciudadDestino;

    @NotBlank(message = "Descripción de mercadería es requerida")
    @Column(name = "descripcion_mercaderia", nullable = false, length = 1000)
    private String descripcionMercaderia;

    @Column(name = "numero_contenedor_guia", length = 100)
    private String numeroContenedorGuia;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.REGISTRADO;

    @Column(name = "codigo_tracking", unique = true, length = 50)
    private String codigoTracking;

    @Column(name = "fecha_estimada_llegada")
    private LocalDate fechaEstimadaLlegada;

    @Column(name = "fecha_llegada_real")
    private LocalDate fechaLlegadaReal;

    @Column(name = "peso_total_kg")
    private Double pesoTotalKg;

    @Column(name = "volumen_total_m3")
    private Double volumenTotalM3;

    @Column(name = "valor_declarado")
    private Double valorDeclarado;

    @Column(name = "moneda", length = 10)
    private String moneda;

    @Column(name = "numero_bl_awb", length = 100)
    private String numeroBlAwb; // Bill of Lading o Air Waybill

    @Column(name = "puerto_embarque", length = 100)
    private String puertoEmbarque;

    @Column(name = "puerto_destino", length = 100)
    private String puertoDestino;

    @Column(name = "empresa_transporte", length = 200)
    private String empresaTransporte;

    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @Builder.Default
    @Column(name = "requiere_seguro")
    private Boolean requiereSeguro = false;

    @Column(name = "valor_seguro")
    private Double valorSeguro;

    // Relaciones
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Producto> productos = new ArrayList<>();

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Factura factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id")
    private Container container;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    @Builder.Default
    private List<HistorialEstado> historialEstados = new ArrayList<>();

    // Campos adicionales para facturación
    @Column(name = "sub_total", precision = 15, scale = 2)
    private java.math.BigDecimal subTotal;

    @Column(name = "iva", precision = 15, scale = 2)
    private java.math.BigDecimal iva;

    @Column(name = "total", precision = 15, scale = 2)
    private java.math.BigDecimal total;

    @Column(name = "direccion_entrega", length = 500)
    private String direccionEntrega;

    @Column(name = "forma_pago", length = 50)
    private String formaPago;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;

    @Column(name = "fecha_entrega_estimada")
    private LocalDateTime fechaEntregaEstimada;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetallePedido> detalles = new ArrayList<>();

    // Helper methods
    public void addDetalle(DetallePedido detalle) {
        detalles.add(detalle);
        detalle.setPedido(this);
    }

    public void removeDetalle(DetallePedido detalle) {
        detalles.remove(detalle);
        detalle.setPedido(null);
    }

    public void addProducto(Producto producto) {
        productos.add(producto);
        producto.setPedido(this);
    }

    public void removeProducto(Producto producto) {
        productos.remove(producto);
        producto.setPedido(null);
    }

    public void cambiarEstado(EstadoPedido nuevoEstado, String comentario) {
        EstadoPedido estadoAnterior = this.estado;
        this.estado = nuevoEstado;

        HistorialEstado historial = HistorialEstado.builder()
                .pedido(this)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(nuevoEstado)
                .comentario(comentario)
                .fechaCambio(LocalDateTime.now())
                .build();

        historialEstados.add(historial);
    }

    public void calcularPesoVolumen() {
        this.pesoTotalKg = productos.stream()
                .filter(p -> p.getPesoKg() != null)
                .mapToDouble(p -> p.getPesoKg() * (p.getCantidadPorUnidad() != null ? p.getCantidadPorUnidad() : 1))
                .sum();

        this.volumenTotalM3 = productos.stream()
                .filter(p -> p.getVolumenM3() != null)
                .mapToDouble(p -> p.getVolumenM3() * (p.getCantidadPorUnidad() != null ? p.getCantidadPorUnidad() : 1))
                .sum();
    }

    @PrePersist
    protected void onCreatePedido() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = EstadoPedido.REGISTRADO;
        }
        if (this.codigoTracking == null) {
            this.codigoTracking = generarCodigoTracking();
        }
    }

    private String generarCodigoTracking() {
        return "TRK-" + System.currentTimeMillis();
    }
}
