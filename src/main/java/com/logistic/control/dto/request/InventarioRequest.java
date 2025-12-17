package com.logistic.control.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioRequest {

    @NotNull(message = "Cliente es requerido")
    private Long clienteId;

    @NotNull(message = "Producto es requerido")
    private Long productoId;

    private String ubicacionDeposito;

    private String zona;

    private String pasillo;

    private String rack;

    private String nivel;

    @NotNull(message = "Cantidad es requerida")
    @Min(value = 0, message = "Cantidad debe ser mayor o igual a 0")
    private Integer cantidad;

    private String lote;

    private LocalDateTime fechaVencimiento;

    private Double costoAlmacenajeDiario;

    private String observaciones;
}
