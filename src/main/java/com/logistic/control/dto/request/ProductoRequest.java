package com.logistic.control.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequest {

    @NotBlank(message = "Código es requerido")
    private String codigo;

    @NotBlank(message = "Descripción es requerida")
    private String descripcion;

    private String descripcionDetallada;

    private String codigoNcm;

    private String codigoArancel;

    @NotNull(message = "Peso es requerido")
    private Double pesoKg;

    private Double volumenM3;

    private String unidadMedida;

    private Integer cantidadPorUnidad;

    private String paisOrigen;

    private Double valorUnitario;

    private String moneda;

    private Boolean esPeligroso;

    private Boolean esPerecedero;

    private Boolean esFragil;

    private Boolean requiereRefrigeracion;

    private Double temperaturaMin;

    private Double temperaturaMax;

    private String observaciones;
}
