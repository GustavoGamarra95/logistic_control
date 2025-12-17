package com.logistic.control.exception;

/**
 * Excepción lanzada cuando no hay stock suficiente
 */
public class StockInsuficienteException extends BusinessException {
    
    public StockInsuficienteException(String producto, Integer disponible, Integer requerido) {
        super(String.format("Stock insuficiente para producto '%s'. Disponible: %d, Requerido: %d", 
                producto, disponible, requerido), "STOCK_INSUFICIENTE");
    }
    
    public StockInsuficienteException(String message) {
        super(message, "STOCK_INSUFICIENTE");
    }
}
