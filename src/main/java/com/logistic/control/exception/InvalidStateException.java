package com.logistic.control.exception;

/**
 * Excepción lanzada cuando una operación no es válida en el estado actual
 */
public class InvalidStateException extends BusinessException {
    
    public InvalidStateException(String message) {
        super(message, "INVALID_STATE");
    }
    
    public InvalidStateException(String entity, String currentState, String operation) {
        super(String.format("No se puede %s en estado '%s' para %s", 
                operation, currentState, entity), "INVALID_STATE");
    }
}
