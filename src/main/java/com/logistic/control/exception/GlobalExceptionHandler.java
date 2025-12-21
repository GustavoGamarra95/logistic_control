package com.logistic.control.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manejador global de excepciones
 * Evita exponer información sensible del sistema en las respuestas
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación de beans
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String errorId = UUID.randomUUID().toString();
        log.warn("Validation error [{}]: {}", errorId, errors);

        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Los datos proporcionados no son válidos")
                .path(request.getRequestURI())
                .validationErrors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja errores de autenticación (credenciales inválidas)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        log.warn("Authentication failed [{}] from IP: {}", 
                errorId, getClientIp(request));

        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Authentication Failed")
                .message("Usuario o contraseña incorrectos")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Maneja errores de usuario no encontrado
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(
            UsernameNotFoundException ex,
            HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        log.warn("User not found [{}]", errorId);

        // NO exponer si el usuario existe o no (prevenir enumeración)
        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Authentication Failed")
                .message("Usuario o contraseña incorrectos")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Maneja errores de acceso denegado
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        log.warn("Access denied [{}] for path: {} from IP: {}", 
                errorId, request.getRequestURI(), getClientIp(request));

        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Access Denied")
                .message("No tiene permisos para acceder a este recurso")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Maneja errores de seguridad (inputs maliciosos)
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(
            SecurityException ex,
            HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        log.error("Security violation [{}]: {} from IP: {}", 
                errorId, ex.getMessage(), getClientIp(request));

        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Security Violation")
                .message("La solicitud fue rechazada por razones de seguridad")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja errores de tipo de argumento incorrecto
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        log.warn("Type mismatch [{}]: {}", errorId, ex.getName());

        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Parameter Type")
                .message("El tipo de dato proporcionado no es válido")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        log.warn("Illegal argument [{}]: {}", errorId, ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Argument")
                .message("Los argumentos proporcionados no son válidos")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja errores de parámetros de ordenamiento inválidos
     */
    @ExceptionHandler(org.springframework.data.mapping.PropertyReferenceException.class)
    public ResponseEntity<ErrorResponse> handlePropertyReferenceException(
            org.springframework.data.mapping.PropertyReferenceException ex,
            HttpServletRequest request) {

        String errorId = UUID.randomUUID().toString();
        log.warn("Invalid sort parameter [{}]: {}", errorId, ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Sort Parameter")
                .message("El parámetro de ordenamiento no es válido. Verifique el nombre del campo.")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja excepciones genéricas
     * NO expone detalles internos del sistema
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        
        // Log completo solo en servidor (NUNCA en respuesta)
        log.error("Unexpected error [{}]: ", errorId, ex);

        ErrorResponse response = ErrorResponse.builder()
                .errorId(errorId)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ha ocurrido un error interno. Por favor contacte al administrador con el código: " + errorId)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Obtiene la IP del cliente considerando proxies
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }

    /**
     * Clase interna para respuesta de error estandarizada
     */
    @lombok.Data
    @lombok.Builder
    public static class ErrorResponse {
        private String errorId;
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, String> validationErrors;
    }
}
