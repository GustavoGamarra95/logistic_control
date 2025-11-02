package com.logistic.control.enums;

/**
 * Tipos de containers según estándares ISO
 */
public enum TipoContainer {
    VEINTE_PIES("20'", 20),
    CUARENTA_PIES("40'", 40),
    CUARENTA_PIES_HC("40' HC", 40),
    CUARENTA_Y_CINCO_PIES("45'", 45),
    REFRIGERADO_20("20' Reefer", 20),
    REFRIGERADO_40("40' Reefer", 40),
    OPEN_TOP_20("20' Open Top", 20),
    OPEN_TOP_40("40' Open Top", 40),
    FLAT_RACK_20("20' Flat Rack", 20),
    FLAT_RACK_40("40' Flat Rack", 40);

    private final String descripcion;
    private final int pies;

    TipoContainer(String descripcion, int pies) {
        this.descripcion = descripcion;
        this.pies = pies;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPies() {
        return pies;
    }
}
