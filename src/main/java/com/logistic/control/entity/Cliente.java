package com.logistic.control.entity;

import com.logistic.control.enums.TipoServicio;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Cliente/Empresa importadora
 * Módulo 1: Gestión de Clientes
 */
@Entity
@Table(name = "clientes", indexes = {
    @Index(name = "idx_cliente_ruc", columnList = "ruc"),
    @Index(name = "idx_cliente_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente extends BaseEntity {

    @NotBlank(message = "Razón social es requerida")
    @Column(name = "razon_social", nullable = false, length = 255)
    private String razonSocial;

    @Column(name = "nombre_fantasia", length = 255)
    private String nombreFantasia;

    @NotBlank(message = "RUC es requerido")
    @Pattern(regexp = "^\\d{6,8}-\\d{1}$", message = "RUC debe tener formato: 12345678-9")
    @Column(name = "ruc", nullable = false, unique = true, length = 20)
    private String ruc;

    @Column(name = "dv", length = 1)
    private String dv;

    @NotBlank(message = "Dirección es requerida")
    @Column(name = "direccion", nullable = false, length = 500)
    private String direccion;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @NotBlank(message = "País es requerido")
    @Column(name = "pais", nullable = false, length = 100)
    private String pais;

    @Column(name = "contacto", length = 200)
    private String contacto;

    @Email(message = "Email debe ser válido")
    @NotBlank(message = "Email es requerido")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "celular", length = 50)
    private String celular;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servicio")
    private TipoServicio tipoServicio;

    @Column(name = "credito_limite")
    private Double creditoLimite;

    @Column(name = "credito_disponible")
    private Double creditoDisponible;

    @Column(name = "es_facturador_electronico")
    private Boolean esFacturadorElectronico = false;

    @Column(name = "estado_ruc", length = 20)
    private String estadoRuc; // ACT, INA, etc. (de siConsRUC)

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    // Relaciones
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Factura> facturas = new ArrayList<>();

    @ManyToMany(mappedBy = "clientes")
    @Builder.Default
    private List<Container> containers = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Inventario> inventarios = new ArrayList<>();

    // Helper methods
    public void addPedido(Pedido pedido) {
        pedidos.add(pedido);
        pedido.setCliente(this);
    }

    public void removePedido(Pedido pedido) {
        pedidos.remove(pedido);
        pedido.setCliente(null);
    }

    public void addFactura(Factura factura) {
        facturas.add(factura);
        factura.setCliente(this);
    }

    public void updateCreditoDisponible(Double monto) {
        if (this.creditoDisponible == null) {
            this.creditoDisponible = this.creditoLimite;
        }
        this.creditoDisponible -= monto;
    }

    public void liberarCredito(Double monto) {
        if (this.creditoDisponible == null) {
            this.creditoDisponible = this.creditoLimite;
        }
        this.creditoDisponible += monto;
    }
}
