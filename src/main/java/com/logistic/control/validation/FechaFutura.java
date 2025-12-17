package com.logistic.control.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validación personalizada para fechas futuras
 */
@Documented
@Constraint(validatedBy = FechaFuturaValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FechaFutura {
    
    String message() default "La fecha debe ser futura";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
