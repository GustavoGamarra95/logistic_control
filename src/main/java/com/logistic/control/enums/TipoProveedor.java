package com.logistic.control.enums;

/**
 * Tipos de proveedores de servicios logísticos
 */
public enum TipoProveedor {
    TRANSPORTE("Transporte"),
    ADUANAL("Agente Aduanal"),
    ALMACEN("Almacén"),
    ALMACENAJE("Almacenaje"),
    SEGURO("Seguros"),
    EMBALAJE("Embalaje"),
    OTRO("Otro");

    private final String descripcion;

    TipoProveedor(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
