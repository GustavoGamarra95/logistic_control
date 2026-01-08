package com.logistic.control.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {

    private Long id;
    private String codigo;
    private String descripcion;
    private String descripcionDetallada;
    private String codigoNcm;
    private String codigoArancel;
    private Double pesoKg;
    private Double volumenM3;
    private String unidadMedida;
    private Integer cantidadPorUnidad;
    private String paisOrigen;
    private Double valorUnitario;
    private String moneda;
    private Integer tasaIva;
    private Double precioVenta;
    private Boolean esPeligroso;
    private Boolean esPerecedero;
    private Boolean esFragil;
    private Boolean requiereRefrigeracion;
    private Double temperaturaMin;
    private Double temperaturaMax;
    private String observaciones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
