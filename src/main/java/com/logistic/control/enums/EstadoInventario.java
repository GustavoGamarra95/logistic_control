package com.logistic.control.enums;

/**
 * Estados de mercadería en inventario/depósito
 */
public enum EstadoInventario {
    EN_TRANSITO("En Tránsito"),
    EN_DEPOSITO("En Depósito"),
    RETENIDO_ADUANA("Retenido en Aduana"),
    DISPONIBLE("Disponible"),
    RESERVADO("Reservado"),
    EN_VERIFICACION("En Verificación"),
    DANIADO("Dañado"),
    DESPACHADO("Despachado");

    private final String descripcion;

    EstadoInventario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
