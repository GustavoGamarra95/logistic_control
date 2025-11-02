package com.logistic.control.entity;

import com.logistic.control.enums.TipoContainer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Container
 * Módulo 3: Gestión de Containers
 */
@Entity
@Table(name = "containers", indexes = {
    @Index(name = "idx_container_numero", columnList = "numero"),
    @Index(name = "idx_container_fecha_salida", columnList = "fecha_salida")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Container extends BaseEntity {

    @NotBlank(message = "Número de container es requerido")
    @Column(name = "numero", nullable = false, unique = true, length = 50)
    private String numero;

    @NotNull(message = "Tipo de container es requerido")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoContainer tipo;

    @Column(name = "peso_kg")
    private Double pesoKg;

    @Column(name = "peso_maximo_kg")
    private Double pesoMaximoKg;

    @Column(name = "volumen_m3")
    private Double volumenM3;

    @Column(name = "volumen_maximo_m3")
    private Double volumenMaximoM3;

    @Column(name = "empresa_transporte", length = 200)
    private String empresaTransporte;

    @Column(name = "empresa_naviera", length = 200)
    private String empresaNaviera;

    @Column(name = "buque_nombre", length = 200)
    private String buqueNombre;

    @Column(name = "viaje_numero", length = 50)
    private String viajeNumero;

    @Column(name = "ruta", length = 500)
    private String ruta;

    @Column(name = "puerto_origen", length = 100)
    private String puertoOrigen;

    @Column(name = "puerto_destino", length = 100)
    private String puertoDestino;

    @Column(name = "fecha_salida")
    private LocalDate fechaSalida;

    @Column(name = "fecha_llegada_estimada")
    private LocalDate fechaLlegadaEstimada;

    @Column(name = "fecha_llegada_real")
    private LocalDate fechaLlegadaReal;

    @Column(name = "consolidado")
    @Builder.Default
    private Boolean consolidado = false;

    @Column(name = "en_transito")
    @Builder.Default
    private Boolean enTransito = false;

    @Column(name = "en_puerto")
    @Builder.Default
    private Boolean enPuerto = false;

    @Column(name = "en_aduana")
    @Builder.Default
    private Boolean enAduana = false;

    @Column(name = "liberado")
    @Builder.Default
    private Boolean liberado = false;

    @Column(name = "numero_bl", length = 100)
    private String numeroBl; // Bill of Lading

    @Column(name = "fecha_emision_bl")
    private LocalDate fechaEmisionBl;

    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    // Relaciones
    @ManyToMany
    @JoinTable(
        name = "container_clientes",
        joinColumns = @JoinColumn(name = "container_id"),
        inverseJoinColumns = @JoinColumn(name = "cliente_id")
    )
    @Builder.Default
    private List<Cliente> clientes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "container_productos",
        joinColumns = @JoinColumn(name = "container_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    @Builder.Default
    private List<Producto> productos = new ArrayList<>();

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Inventario> inventarios = new ArrayList<>();

    // Helper methods
    public void addCliente(Cliente cliente) {
        clientes.add(cliente);
        cliente.getContainers().add(this);
    }

    public void removeCliente(Cliente cliente) {
        clientes.remove(cliente);
        cliente.getContainers().remove(this);
    }

    public void addProducto(Producto producto) {
        productos.add(producto);
        producto.getContainers().add(this);
    }

    public void removeProducto(Producto producto) {
        productos.remove(producto);
        producto.getContainers().remove(this);
    }

    public void consolidar() {
        this.consolidado = true;
        calcularPesoVolumen();
    }

    public void calcularPesoVolumen() {
        this.pesoKg = productos.stream()
                .mapToDouble(p -> p.getPesoKg() * (p.getCantidadPorUnidad() != null ? p.getCantidadPorUnidad() : 1))
                .sum();

        this.volumenM3 = productos.stream()
                .filter(p -> p.getVolumenM3() != null)
                .mapToDouble(p -> p.getVolumenM3() * (p.getCantidadPorUnidad() != null ? p.getCantidadPorUnidad() : 1))
                .sum();
    }

    public Double getCapacidadDisponiblePeso() {
        if (pesoMaximoKg == null || pesoKg == null) return null;
        return pesoMaximoKg - pesoKg;
    }

    public Double getCapacidadDisponibleVolumen() {
        if (volumenMaximoM3 == null || volumenM3 == null) return null;
        return volumenMaximoM3 - volumenM3;
    }

    public Double getPorcentajeOcupacionPeso() {
        if (pesoMaximoKg == null || pesoKg == null || pesoMaximoKg == 0) return 0.0;
        return (pesoKg / pesoMaximoKg) * 100;
    }

    public Double getPorcentajeOcupacionVolumen() {
        if (volumenMaximoM3 == null || volumenM3 == null || volumenMaximoM3 == 0) return 0.0;
        return (volumenM3 / volumenMaximoM3) * 100;
    }
}
