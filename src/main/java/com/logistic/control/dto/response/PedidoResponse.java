package com.logistic.control.dto.response;

import com.logistic.control.enums.EstadoPedido;
import com.logistic.control.enums.TipoCarga;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private LocalDateTime fechaRegistro;
    private TipoCarga tipoCarga;
    private String paisOrigen;
    private String paisDestino;
    private String ciudadOrigen;
    private String ciudadDestino;
    private String descripcionMercaderia;
    private String numeroContenedorGuia;
    private EstadoPedido estado;
    private String codigoTracking;
    private LocalDate fechaEstimadaLlegada;
    private LocalDate fechaLlegadaReal;
    private Double pesoTotalKg;
    private Double volumenTotalM3;
    private Double valorDeclarado;
    private String moneda;
    private String numeroBlAwb;
    private String puertoEmbarque;
    private String puertoDestino;
    private String empresaTransporte;
    private Boolean requiereSeguro;
    private Double valorSeguro;
    private String observaciones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
