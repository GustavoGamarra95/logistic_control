package com.logistic.control.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.logistic.control.enums.EstadoInventario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioResponse {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private Long productoId;
    private String productoDescripcion;
    private String ubicacionDeposito;
    private String zona;
    private String pasillo;
    private String rack;
    private String nivel;
    private Integer cantidad;
    private Integer cantidadReservada;
    private Integer cantidadDisponible;
    private EstadoInventario estado;
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;
    private String lote;
    private LocalDateTime fechaVencimiento;
    private Integer diasAlmacenaje;
    private Double costoAlmacenajeDiario;
    private Double costoAlmacenajeTotal;
    private String observaciones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Genera la ubicación completa concatenando todos los campos de ubicación
     * Formato: DEPOSITO-ZONA-PASILLO-RACK-NIVEL
     */
    public String getUbicacion() {
        List<String> parts = new ArrayList<>();

        if (ubicacionDeposito != null && !ubicacionDeposito.isEmpty()) {
            parts.add(ubicacionDeposito);
        }
        if (zona != null && !zona.isEmpty()) {
            parts.add(zona);
        }
        if (pasillo != null && !pasillo.isEmpty()) {
            parts.add(pasillo);
        }
        if (rack != null && !rack.isEmpty()) {
            parts.add(rack);
        }
        if (nivel != null && !nivel.isEmpty()) {
            parts.add(nivel);
        }

        return parts.isEmpty() ? "SIN UBICACIÓN" : String.join("-", parts);
    }
}
