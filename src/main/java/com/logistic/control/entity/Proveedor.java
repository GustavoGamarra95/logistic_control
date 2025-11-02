package com.logistic.control.entity;

import com.logistic.control.enums.TipoProveedor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Proveedor (Transportistas, Aduaneros, etc.)
 * Módulo 6: Proveedores y Transportistas
 */
@Entity
@Table(name = "proveedores", indexes = {
    @Index(name = "idx_proveedor_ruc", columnList = "ruc"),
    @Index(name = "idx_proveedor_tipo", columnList = "tipo")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor extends BaseEntity {

    @NotBlank(message = "Nombre es requerido")
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "razon_social", length = 255)
    private String razonSocial;

    @Column(name = "ruc", length = 20)
    private String ruc;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoProveedor tipo;

    @Column(name = "direccion", length = 500)
    private String direccion;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "pais", length = 100)
    private String pais;

    @Column(name = "contacto", length = 200)
    private String contacto;

    @Email
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "costo_servicio")
    private Double costoServicio;

    @Column(name = "moneda", length = 10)
    @Builder.Default
    private String moneda = "PYG";

    @Column(name = "plazo_pago_dias")
    private Integer plazoPagoDias;

    @Column(name = "cuenta_bancaria", length = 100)
    private String cuentaBancaria;

    @Column(name = "banco", length = 100)
    private String banco;

    @Column(name = "calificacion")
    private Double calificacion; // 1-5 estrellas

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    // Relaciones
    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FacturaProveedor> facturas = new ArrayList<>();

    // Helper methods
    public void addFactura(FacturaProveedor factura) {
        facturas.add(factura);
        factura.setProveedor(this);
    }
}
