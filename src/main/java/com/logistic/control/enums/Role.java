package com.logistic.control.enums;

/**
 * Roles de usuario para seguridad
 */
public enum Role {
    ADMIN("Administrador", "Acceso total al sistema"),
    OPERADOR("Operador", "Gestión de pedidos, clientes, inventario"),
    CLIENTE("Cliente", "Consulta de sus pedidos y facturas"),
    FINANZAS("Finanzas", "Gestión de facturación y reportes financieros"),
    DEPOSITO("Depósito", "Gestión de inventario y movimientos");

    private final String nombre;
    private final String descripcion;

    Role(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
