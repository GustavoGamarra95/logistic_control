package com.logistic.control.entity;

import com.logistic.control.enums.EstadoFactura;
import com.logistic.control.enums.TipoFactura;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Factura (Documento Electrónico SIFEN)
 * Módulo 5: Facturación con SIFEN
 */
@Entity
@Table(name = "facturas", indexes = {
    @Index(name = "idx_factura_numero", columnList = "numero_factura"),
    @Index(name = "idx_factura_cdc", columnList = "cdc"),
    @Index(name = "idx_factura_cliente", columnList = "cliente_id"),
    @Index(name = "idx_factura_estado", columnList = "estado"),
    @Index(name = "idx_factura_fecha", columnList = "fecha_emision")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factura extends BaseEntity {

    @Column(name = "numero_factura", unique = true, length = 50)
    private String numeroFactura; // Formato: 001-001-0000001

    @NotNull(message = "Fecha de emisión es requerida")
    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @NotNull(message = "Cliente es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_factura", length = 50)
    @Builder.Default
    private TipoFactura tipoFactura = TipoFactura.FACTURA_VENTA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_original_id")
    private Factura facturaOriginal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devolucion_id")
    private DevolucionVenta devolucion;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal;

    @Column(name = "iva_5")
    private Double iva5;

    @Column(name = "iva_10")
    private Double iva10;

    @Column(name = "total_iva")
    private Double totalIva;

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "descuento")
    @Builder.Default
    private Double descuento = 0.0;

    @Column(name = "moneda", length = 10)
    @Builder.Default
    private String moneda = "PYG";

    @Column(name = "tipo_cambio")
    private Double tipoCambio;

    @Column(name = "tipo", length = 20)
    private String tipo; // CONTADO o CREDITO

    @Column(name = "condicion_pago", length = 200)
    private String condicionPago; // Ej: "30 días", "Pago contra entrega", etc.

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoFactura estado = EstadoFactura.BORRADOR;

    // Campos SIFEN
    @Column(name = "cdc", unique = true, length = 44)
    private String cdc; // Código de Control del Documento (44 dígitos)

    @Column(name = "timbrado", length = 20)
    private String timbrado;

    @Column(name = "establecimiento", length = 10)
    private String establecimiento;

    @Column(name = "punto_expedicion", length = 10)
    private String puntoExpedicion;

    @Column(name = "tipo_documento", length = 10)
    @Builder.Default
    private String tipoDocumento = "1"; // 1=Factura Electrónica

    @Column(name = "fecha_envio_sifen")
    private LocalDateTime fechaEnvioSifen;

    @Column(name = "fecha_aprobacion_sifen")
    private LocalDateTime fechaAprobacionSifen;

    @Column(name = "xml_de", columnDefinition = "TEXT")
    private String xmlDe; // XML del DE generado

    @Column(name = "xml_de_firmado", columnDefinition = "TEXT")
    private String xmlDeFirmado; // XML firmado con XAdES-BES

    @Column(name = "respuesta_sifen", columnDefinition = "TEXT")
    private String respuestaSifen;

    @Column(name = "codigo_estado_sifen", length = 10)
    private String codigoEstadoSifen; // Código de respuesta SIFEN

    @Column(name = "mensaje_sifen", length = 500)
    private String mensajeSifen;

    @Column(name = "qr_code", columnDefinition = "TEXT")
    private String qrCode; // Base64 del QR para KuDE

    @Column(name = "url_kude", length = 500)
    private String urlKude; // URL para consulta pública

    // Pagos
    @Column(name = "saldo")
    private Double saldo;

    @Column(name = "pagado")
    @Builder.Default
    private Double pagado = 0.0;

    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    // Relaciones
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleFactura> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Pago> pagos = new ArrayList<>();

    // Helper methods
    public void addDetalle(DetalleFactura detalle) {
        detalles.add(detalle);
        detalle.setFactura(this);
    }

    public void removeDetalle(DetalleFactura detalle) {
        detalles.remove(detalle);
        detalle.setFactura(null);
    }

    public void calcularTotales() {
        this.subtotal = detalles.stream()
                .mapToDouble(DetalleFactura::getTotal)
                .sum();

        this.iva5 = detalles.stream()
                .filter(d -> d.getPorcentajeIva() != null && d.getPorcentajeIva() == 5)
                .mapToDouble(d -> d.getTotal() * 0.05)
                .sum();

        this.iva10 = detalles.stream()
                .filter(d -> d.getPorcentajeIva() != null && d.getPorcentajeIva() == 10)
                .mapToDouble(d -> d.getTotal() * 0.10)
                .sum();

        this.totalIva = this.iva5 + this.iva10;
        this.total = this.subtotal + this.totalIva - this.descuento;
        this.saldo = this.total - this.pagado;
    }

    public void registrarPago(Pago pago) {
        pagos.add(pago);
        pago.setFactura(this);
        this.pagado += pago.getMonto();
        this.saldo = this.total - this.pagado;

        if (this.saldo <= 0) {
            this.estado = EstadoFactura.PAGADA;
        } else if (this.pagado > 0) {
            this.estado = EstadoFactura.PAGADA_PARCIAL;
        }
    }

    public void aprobarSifen(String cdc, String respuesta) {
        this.cdc = cdc;
        this.estado = EstadoFactura.APROBADA;
        this.fechaAprobacionSifen = LocalDateTime.now();
        this.respuestaSifen = respuesta;
        this.codigoEstadoSifen = "0100"; // Código de aprobación SIFEN
    }

    public void rechazarSifen(String codigoError, String mensaje) {
        this.estado = EstadoFactura.RECHAZADA;
        this.codigoEstadoSifen = codigoError;
        this.mensajeSifen = mensaje;
    }

    public void anular() {
        this.estado = EstadoFactura.ANULADA;
    }

    /**
     * Verifica si esta factura es una nota de crédito.
     */
    public boolean esNotaCredito() {
        return this.tipoFactura == TipoFactura.NOTA_CREDITO;
    }

    /**
     * Valida que una nota de crédito tenga los datos correctos.
     */
    public void validarNotaCredito() {
        if (!esNotaCredito()) {
            return;
        }

        if (facturaOriginal == null) {
            throw new IllegalStateException("Una nota de crédito debe tener una factura original");
        }

        if (this.total > facturaOriginal.getTotal()) {
            throw new IllegalStateException(
                String.format("El total de la nota de crédito (%.2f) no puede exceder el total de la factura original (%.2f)",
                             this.total, facturaOriginal.getTotal())
            );
        }

        if (devolucion == null) {
            throw new IllegalStateException("Una nota de crédito debe estar asociada a una devolución");
        }
    }

    @PrePersist
    protected void onCreateFactura() {
        if (this.fechaEmision == null) {
            this.fechaEmision = LocalDateTime.now();
        }
        if (this.moneda == null) {
            this.moneda = "PYG";
        }
        if (this.saldo == null) {
            this.saldo = this.total;
        }
    }
}
