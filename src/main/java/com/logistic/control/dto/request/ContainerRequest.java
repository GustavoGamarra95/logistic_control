package com.logistic.control.dto.request;

import com.logistic.control.enums.TipoContainer;
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
public class ContainerRequest {

    @NotBlank(message = "Número de container es requerido")
    private String numero;

    @NotNull(message = "Tipo de container es requerido")
    private TipoContainer tipo;

    private Double pesoMaximoKg;

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

    private String numeroBl;

    private LocalDate fechaEmisionBl;

    private String observaciones;
}
