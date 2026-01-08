package com.logistic.control.dto.response;

import com.logistic.control.enums.TipoContainer;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContainerResponse {

    private Long id;
    private String numero;
    private TipoContainer tipo;
    private String estado; // Estado calculado basado en los flags booleanos
    private Double pesoKg;
    private Double pesoMaximoKg;
    private Double volumenM3;
    private Double volumenMaximoM3;
    private String empresaTransporte;
    private String empresaNaviera;
    private String buqueNombre;
    private String viajeNumero;
    private String ruta;
    private String puertoOrigen;
    private String puertoDestino;
    private LocalDate fechaSalida;
    private LocalDate fechaLlegadaEstimada;
    private LocalDate fechaLlegadaReal;
    private Boolean consolidado;
    private Boolean enTransito;
    private Boolean enPuerto;
    private Boolean enAduana;
    private Boolean liberado;
    private String numeroBl;
    private LocalDate fechaEmisionBl;
    private Double capacidadDisponiblePeso;
    private Double capacidadDisponibleVolumen;
    private Double porcentajeOcupacionPeso;
    private Double porcentajeOcupacionVolumen;
    private String observaciones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
