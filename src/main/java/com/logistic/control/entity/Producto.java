package com.logistic.control.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Producto/Mercadería
 */
@Entity
@Table(name = "productos", indexes = {
    @Index(name = "idx_producto_codigo", columnList = "codigo"),
    @Index(name = "idx_producto_ncm", columnList = "codigo_ncm")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto extends BaseEntity {

    @NotBlank(message = "Código es requerido")
    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @NotBlank(message = "Descripción es requerida")
    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    @Column(name = "descripcion_detallada", length = 2000)
    private String descripcionDetallada;

    @Column(name = "codigo_ncm", length = 20)
    private String codigoNcm; // Nomenclatura Común del Mercosur

    @Column(name = "codigo_arancel", length = 20)
    private String codigoArancel;

    @NotNull(message = "Peso es requerido")
    @Column(name = "peso_kg", nullable = false)
    private Double pesoKg;

    @Column(name = "volumen_m3")
    private Double volumenM3;

    @Column(name = "unidad_medida", length = 20)
    private String unidadMedida;

    @Column(name = "cantidad_por_unidad")
    private Integer cantidadPorUnidad;

    @Column(name = "pais_origen", length = 100)
    private String paisOrigen;

    @Column(name = "valor_unitario")
    private Double valorUnitario;

    @Column(name = "moneda", length = 10)
    private String moneda;

    @Column(name = "es_peligroso")
    private Boolean esPeligroso = false;

    @Column(name = "es_perecedero")
    private Boolean esPerecedero = false;

    @Column(name = "es_fragil")
    private Boolean esFragil = false;

    @Column(name = "requiere_refrigeracion")
    private Boolean requiereRefrigeracion = false;

    @Column(name = "temperatura_min")
    private Double temperaturaMin;

    @Column(name = "temperatura_max")
    private Double temperaturaMax;

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    // Relaciones
    @ManyToMany(mappedBy = "productos")
    @Builder.Default
    private List<Container> containers = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Inventario> inventarios = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DetalleFactura> detallesFactura = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    // Helper methods
    public Double calcularPesoTotal(Integer cantidad) {
        return this.pesoKg * cantidad;
    }

    public Double calcularVolumenTotal(Integer cantidad) {
        if (this.volumenM3 == null) return null;
        return this.volumenM3 * cantidad;
    }

    public Double calcularValorTotal(Integer cantidad) {
        if (this.valorUnitario == null) return null;
        return this.valorUnitario * cantidad;
    }
}
