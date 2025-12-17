package com.logistic.control.dto.response;

import com.logistic.control.enums.EstadoInventario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
}
