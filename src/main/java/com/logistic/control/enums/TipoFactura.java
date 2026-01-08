package com.logistic.control.enums;

/**
 * Tipos de factura/documento fiscal.
 */
public enum TipoFactura {
    FACTURA_VENTA("Factura de Venta"),
    NOTA_CREDITO("Nota de Crédito"),
    NOTA_DEBITO("Nota de Débito");

    private final String descripcion;

    TipoFactura(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
