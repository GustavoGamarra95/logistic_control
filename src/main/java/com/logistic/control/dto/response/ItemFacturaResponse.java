package com.logistic.control.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemFacturaResponse {

    private Long id;
    private String codigo;
    private String descripcion;
    private Integer cantidad;
    private String unidadMedida;
    private Double precioUnitario;
    private Integer tasaIva;
    private Double subtotal;
    private Double montoIva;
    private Double total;
}
