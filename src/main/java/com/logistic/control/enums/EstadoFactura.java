package com.logistic.control.enums;

/**
 * Estados de factura (ciclo completo con SIFEN)
 */
public enum EstadoFactura {
    BORRADOR("Borrador"),
    GENERADA("Generada"),
    ENVIADA_SIFEN("Enviada a SIFEN"),
    APROBADA("Aprobada por SET"),
    RECHAZADA("Rechazada por SET"),
    PAGADA("Pagada"),
    PAGADA_PARCIAL("Pagada Parcial"),
    VENCIDA("Vencida"),
    ANULADA("Anulada"),
    CANCELADA_SIFEN("Cancelada en SIFEN");

    private final String descripcion;

    EstadoFactura(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
