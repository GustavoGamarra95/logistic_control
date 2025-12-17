package com.logistic.control.dto.request;

import com.logistic.control.enums.TipoCarga;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {

    @NotNull(message = "Cliente es requerido")
    private Long clienteId;

    private TipoCarga tipoCarga;

    @NotBlank(message = "País de origen es requerido")
    private String paisOrigen;

    @NotBlank(message = "País de destino es requerido")
    private String paisDestino;

    private String ciudadOrigen;

    private String ciudadDestino;

    @NotBlank(message = "Descripción de mercadería es requerida")
    private String descripcionMercaderia;

    private String numeroContenedorGuia;

    private LocalDate fechaEstimadaLlegada;

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
}
