package com.logistic.control.security;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de Rate Limiting para prevenir brute force y DoS
 */
@Service
public class RateLimitService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(15);
    private static final int MAX_REQUESTS_PER_MINUTE = 60;

    // Almacena intentos de login fallidos por IP/username
    private final Map<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    // Almacena requests por IP para rate limiting general
    private final Map<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();

    /**
     * Registra un intento de login fallido
     */
    public void recordFailedLogin(String key) {
        LoginAttempt attempt = loginAttempts.computeIfAbsent(
            key, 
            k -> new LoginAttempt()
        );

        attempt.incrementAttempts();

        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            attempt.lockUntil(LocalDateTime.now().plus(LOCKOUT_DURATION));
        }
    }

    /**
     * Registra un login exitoso y limpia intentos
     */
    public void recordSuccessfulLogin(String key) {
        loginAttempts.remove(key);
    }

    /**
     * Verifica si una IP/usuario está bloqueado
     */
    public boolean isBlocked(String key) {
        LoginAttempt attempt = loginAttempts.get(key);
        
        if (attempt == null) {
            return false;
        }

        // Si el bloqueo expiró, limpiar
        if (attempt.isLocked() && attempt.getLockUntil().isBefore(LocalDateTime.now())) {
            loginAttempts.remove(key);
            return false;
        }

        return attempt.isLocked();
    }

    /**
     * Obtiene el tiempo restante de bloqueo
     */
    public Duration getTimeUntilUnlock(String key) {
        LoginAttempt attempt = loginAttempts.get(key);
        
        if (attempt == null || !attempt.isLocked()) {
            return Duration.ZERO;
        }

        return Duration.between(LocalDateTime.now(), attempt.getLockUntil());
    }

    /**
     * Verifica rate limiting general por IP
     */
    public boolean allowRequest(String ipAddress) {
        RequestCounter counter = requestCounters.computeIfAbsent(
            ipAddress,
            k -> new RequestCounter()
        );

        // Resetear contador si pasó un minuto
        if (counter.getLastReset().plusMinutes(1).isBefore(LocalDateTime.now())) {
            counter.reset();
        }

        counter.increment();

        return counter.getCount() <= MAX_REQUESTS_PER_MINUTE;
    }

    /**
     * Limpia cachés periódicamente (llamar desde scheduled task)
     */
    public void cleanupExpiredEntries() {
        LocalDateTime now = LocalDateTime.now();

        loginAttempts.entrySet().removeIf(entry -> {
            LoginAttempt attempt = entry.getValue();
            return attempt.isLocked() && attempt.getLockUntil().isBefore(now);
        });

        requestCounters.entrySet().removeIf(entry -> 
            entry.getValue().getLastReset().plusMinutes(5).isBefore(now)
        );
    }

    /**
     * Clase interna para tracking de intentos de login
     */
    private static class LoginAttempt {
        private int attempts = 0;
        private LocalDateTime lockUntil;

        public void incrementAttempts() {
            attempts++;
        }

        public int getAttempts() {
            return attempts;
        }

        public void lockUntil(LocalDateTime until) {
            this.lockUntil = until;
        }

        public boolean isLocked() {
            return lockUntil != null && lockUntil.isAfter(LocalDateTime.now());
        }

        public LocalDateTime getLockUntil() {
            return lockUntil;
        }
    }

    /**
     * Clase interna para contador de requests
     */
    private static class RequestCounter {
        private int count = 0;
        private LocalDateTime lastReset = LocalDateTime.now();

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }

        public LocalDateTime getLastReset() {
            return lastReset;
        }

        public void reset() {
            count = 0;
            lastReset = LocalDateTime.now();
        }
    }
}
