package com.logistic.control.dto.request;

import jakarta.validation.constraints.Min;
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
public class ItemFacturaRequest {

    private String codigo;

    @NotBlank(message = "Descripción es requerida")
    private String descripcion;

    @NotNull(message = "Cantidad es requerida")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @NotBlank(message = "Unidad de medida es requerida")
    private String unidadMedida;

    @NotNull(message = "Precio unitario es requerido")
    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    private Double precioUnitario;

    @NotNull(message = "Tasa de IVA es requerida")
    @Min(value = 0, message = "Tasa de IVA inválida")
    private Integer tasaIva;
}
