package com.logistic.control.dto.request;

import com.logistic.control.enums.TipoServicio;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequest {

    @NotBlank(message = "Razón social es requerida")
    private String razonSocial;

    private String nombreFantasia;

    @NotBlank(message = "RUC es requerido")
    // Acepta formato con DV (12345678-9) o solo el número de RUC (12345678)
    @Pattern(regexp = "^\\d{6,8}(-\\d{1})?$", message = "RUC debe tener formato: 12345678-9 o 12345678 (DV separado)")
    private String ruc;

    private String dv;

    @NotBlank(message = "Dirección es requerida")
    private String direccion;

    private String ciudad;

    @NotBlank(message = "País es requerido")
    private String pais;

    private String contacto;

    @Email(message = "Email debe ser válido")
    @NotBlank(message = "Email es requerido")
    private String email;

    private String telefono;

    private String celular;

    private TipoServicio tipoServicio;

    private Double creditoLimite;

    private Boolean esFacturadorElectronico;

    private String observaciones;
}
