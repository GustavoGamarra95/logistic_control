package com.logistic.control.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Roles de usuario para seguridad y control de acceso
 */
@Schema(description = "Roles de usuario en el sistema")
public enum Role {
    @Schema(description = "Administrador - Acceso total al sistema")
    ADMIN("Administrador", "Acceso total al sistema"),
    
    @Schema(description = "Operador - Gestión de pedidos, clientes, inventario")
    OPERADOR("Operador", "Gestión de pedidos, clientes, inventario"),
    
    @Schema(description = "Cliente - Consulta de sus pedidos y facturas")
    CLIENTE("Cliente", "Consulta de sus pedidos y facturas"),
    
    @Schema(description = "Finanzas - Gestión de facturación y reportes financieros")
    FINANZAS("Finanzas", "Gestión de facturación y reportes financieros"),
    
    @Schema(description = "Depósito - Gestión de inventario y movimientos")
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
