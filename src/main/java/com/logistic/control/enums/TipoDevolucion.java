package com.logistic.control.enums;

/**
 * Tipos de devolución en el sistema.
 */
public enum TipoDevolucion {
    /**
     * Devolución de producto físico.
     * Afecta inventario + nota de crédito opcional.
     */
    PRODUCTO_FISICO("Devolución de Producto Físico"),

    /**
     * Anulación o corrección de factura.
     * Sin movimiento físico, solo ajuste documental.
     */
    CORRECCION_FACTURA("Anulación/Corrección de Factura"),

    /**
     * Devolución parcial de pedido pre-factura.
     * Modifica el pedido original antes de facturación completa.
     */
    AJUSTE_PEDIDO("Ajuste de Pedido Pre-Factura");

    private final String descripcion;

    TipoDevolucion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
