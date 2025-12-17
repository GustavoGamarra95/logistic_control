package com.logistic.control.dto.response;

import com.logistic.control.enums.TipoServicio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {

    private Long id;
    private String razonSocial;
    private String nombreFantasia;
    private String ruc;
    private String dv;
    private String direccion;
    private String ciudad;
    private String pais;
    private String contacto;
    private String email;
    private String telefono;
    private String celular;
    private TipoServicio tipoServicio;
    private Double creditoLimite;
    private Double creditoDisponible;
    private Boolean esFacturadorElectronico;
    private String estadoRuc;
    private String observaciones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
