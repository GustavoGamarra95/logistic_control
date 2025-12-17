package com.logistic.control.dto.response;

import com.logistic.control.enums.TipoProveedor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorResponse {

    private Long id;
    private String nombre;
    private String razonSocial;
    private String ruc;
    private TipoProveedor tipo;
    private String direccion;
    private String ciudad;
    private String pais;
    private String contacto;
    private String email;
    private String telefono;
    private Double costoServicio;
    private String moneda;
    private Integer plazoPagoDias;
    private String cuentaBancaria;
    private String banco;
    private Double calificacion;
    private String observaciones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
