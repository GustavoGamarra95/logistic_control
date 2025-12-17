package com.logistic.control.dto.request;

import com.logistic.control.enums.TipoProveedor;
import jakarta.validation.constraints.Email;
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
public class ProveedorRequest {

    @NotBlank(message = "Nombre es requerido")
    private String nombre;

    private String razonSocial;

    private String ruc;

    @NotNull(message = "Tipo es requerido")
    private TipoProveedor tipo;

    private String direccion;

    private String ciudad;

    private String pais;

    private String contacto;

    @Email
    private String email;

    private String telefono;

    private Double costoServicio;

    private String moneda;

    private Integer plazoPagoDias;

    private String cuentaBancaria;

    private String banco;

    private String observaciones;
}
