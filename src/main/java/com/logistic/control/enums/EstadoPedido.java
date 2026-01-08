package com.logistic.control.enums;

/**
 * Estados posibles de un pedido/envío
 */
public enum EstadoPedido {
    REGISTRADO("Registrado"),
    EN_TRANSITO("En Tránsito"),
    RECIBIDO("Recibido"),
    EN_ADUANA("En Aduana"),
    LIBERADO("Liberado"),
    EN_DEPOSITO("En Depósito"),
    EN_REPARTO("En Reparto"),
    ENTREGADO("Entregado"),
    FACTURADO("Facturado"),
    CANCELADO("Cancelado"),
    DEVUELTO("Devuelto");

    private final String descripcion;

    EstadoPedido(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
