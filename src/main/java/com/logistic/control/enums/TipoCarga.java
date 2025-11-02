package com.logistic.control.enums;

/**
 * Tipos de carga transportada
 */
public enum TipoCarga {
    FCL("Full Container Load"),
    LCL("Less than Container Load"),
    SUELTA("Carga Suelta"),
    GRANEL("Granel"),
    PERECEDERA("Perecedera"),
    PELIGROSA("Peligrosa"),
    FRAGIL("Frágil"),
    GENERAL("General");

    private final String descripcion;

    TipoCarga(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
