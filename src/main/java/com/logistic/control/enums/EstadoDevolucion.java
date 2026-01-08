package com.logistic.control.enums;

/**
 * Estados posibles de una devolución.
 */
public enum EstadoDevolucion {
    SOLICITADA("Solicitada"),
    EN_REVISION("En Revisión"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada"),
    EN_PROCESO("En Proceso"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada");

    private final String descripcion;

    EstadoDevolucion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
