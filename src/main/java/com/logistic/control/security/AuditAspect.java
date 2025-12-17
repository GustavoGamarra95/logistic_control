package com.logistic.control.security;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Aspect para auditoría de operaciones críticas
 * NO loggea datos sensibles (contraseñas, tokens, PII completa)
 */
@Slf4j
@Aspect
@Component
public class AuditAspect {

    /**
     * Audita operaciones de autenticación
     */
    @Around("execution(* com.logistic.control.controller.AuthController.*(..))")
    public Object auditAuthOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String username = getCurrentUsername();
        
        log.info("AUTH_OPERATION: method={}, user={}, timestamp={}", 
                methodName, maskUsername(username), LocalDateTime.now());
        
        try {
            Object result = joinPoint.proceed();
            log.info("AUTH_SUCCESS: method={}, user={}", methodName, maskUsername(username));
            return result;
        } catch (Exception e) {
            log.warn("AUTH_FAILED: method={}, user={}, error={}", 
                    methodName, maskUsername(username), e.getClass().getSimpleName());
            throw e;
        }
    }

    /**
     * Audita operaciones CRUD críticas
     */
    @Around("execution(* com.logistic.control.controller.*.create*(..)) || " +
            "execution(* com.logistic.control.controller.*.update*(..)) || " +
            "execution(* com.logistic.control.controller.*.delete*(..))")
    public Object auditCrudOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String username = getCurrentUsername();
        
        // NO loggear los argumentos completos (pueden contener datos sensibles)
        Object[] args = joinPoint.getArgs();
        String argsInfo = args.length > 0 ? "args_count=" + args.length : "no_args";
        
        log.info("CRUD_OPERATION: class={}, method={}, user={}, {}, timestamp={}", 
                className, methodName, maskUsername(username), argsInfo, LocalDateTime.now());
        
        try {
            Object result = joinPoint.proceed();
            log.info("CRUD_SUCCESS: class={}, method={}, user={}", 
                    className, methodName, maskUsername(username));
            return result;
        } catch (Exception e) {
            log.error("CRUD_FAILED: class={}, method={}, user={}, error={}", 
                    className, methodName, maskUsername(username), e.getClass().getSimpleName());
            throw e;
        }
    }

    /**
     * Audita acceso a datos de usuarios
     */
    @Around("execution(* com.logistic.control.controller.UsuarioController.*(..))")
    public Object auditUserDataAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String username = getCurrentUsername();
        
        log.info("USER_DATA_ACCESS: method={}, accessor={}, timestamp={}", 
                methodName, maskUsername(username), LocalDateTime.now());
        
        Object result = joinPoint.proceed();
        
        log.info("USER_DATA_ACCESS_SUCCESS: method={}, accessor={}", 
                methodName, maskUsername(username));
        
        return result;
    }

    /**
     * Audita operaciones de facturación (críticas para negocio)
     */
    @Around("execution(* com.logistic.control.controller.FacturaController.*(..))")
    public Object auditBillingOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String username = getCurrentUsername();
        
        log.info("BILLING_OPERATION: method={}, user={}, timestamp={}", 
                methodName, maskUsername(username), LocalDateTime.now());
        
        try {
            Object result = joinPoint.proceed();
            log.info("BILLING_SUCCESS: method={}, user={}", methodName, maskUsername(username));
            return result;
        } catch (Exception e) {
            log.error("BILLING_FAILED: method={}, user={}, error={}", 
                    methodName, maskUsername(username), e.getClass().getSimpleName());
            throw e;
        }
    }

    /**
     * Obtiene el username del usuario autenticado
     */
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "anonymous";
    }

    /**
     * Enmascara el username para no exponer información completa en logs
     * Ejemplo: "usuario123" -> "usu****123"
     */
    private String maskUsername(String username) {
        if (username == null || username.length() <= 6 || "anonymous".equals(username)) {
            return username;
        }
        
        int len = username.length();
        String start = username.substring(0, 3);
        String end = username.substring(len - 3);
        
        return start + "****" + end;
    }

    /**
     * Sanitiza argumentos para logging (remueve datos sensibles)
     */
    private String sanitizeForLog(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        String str = obj.toString();
        
        // Remover patrones de datos sensibles
        str = str.replaceAll("password=[^,\\s)]+", "password=***");
        str = str.replaceAll("token=[^,\\s)]+", "token=***");
        str = str.replaceAll("secret=[^,\\s)]+", "secret=***");
        str = str.replaceAll("apiKey=[^,\\s)]+", "apiKey=***");
        
        return str.length() > 200 ? str.substring(0, 200) + "..." : str;
    }
}
