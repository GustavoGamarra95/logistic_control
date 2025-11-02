package com.logistic.control.enums;

/**
 * Tipo de servicio logístico ofrecido al cliente
 */
public enum TipoServicio {
    AEREO("Aéreo"),
    MARITIMO("Marítimo"),
    TERRESTRE("Terrestre"),
    MULTIMODAL("Multimodal");

    private final String descripcion;

    TipoServicio(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
