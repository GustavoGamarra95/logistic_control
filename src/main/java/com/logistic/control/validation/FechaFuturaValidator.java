package com.logistic.control.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

/**
 * Validador para fechas futuras
 */
public class FechaFuturaValidator implements ConstraintValidator<FechaFutura, LocalDateTime> {

    @Override
    public void initialize(FechaFutura constraintAnnotation) {
        // No requiere inicialización
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Usar @NotNull por separado si es requerido
        }
        return value.isAfter(LocalDateTime.now());
    }
}
