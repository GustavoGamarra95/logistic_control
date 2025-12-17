package com.logistic.control.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro para agregar headers de seguridad HTTP
 * Protege contra XSS, Clickjacking, MIME sniffing, etc.
 */
@Component
public class SecurityHeadersConfig implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Content Security Policy (CSP) - Previene XSS
        httpResponse.setHeader("Content-Security-Policy",
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
            "style-src 'self' 'unsafe-inline'; " +
            "img-src 'self' data: https:; " +
            "font-src 'self' data:; " +
            "connect-src 'self'; " +
            "frame-ancestors 'none';"
        );

        // X-Frame-Options - Previene Clickjacking
        httpResponse.setHeader("X-Frame-Options", "DENY");

        // X-Content-Type-Options - Previene MIME sniffing
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");

        // X-XSS-Protection (legacy pero útil para navegadores antiguos)
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

        // Referrer-Policy - Controla información de referrer
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Permissions-Policy (antes Feature-Policy)
        httpResponse.setHeader("Permissions-Policy",
            "geolocation=(), " +
            "microphone=(), " +
            "camera=(), " +
            "payment=(), " +
            "usb=(), " +
            "magnetometer=(), " +
            "gyroscope=()"
        );

        // Strict-Transport-Security (HSTS) - Forzar HTTPS
        // Solo habilitar en producción con HTTPS configurado
        // httpResponse.setHeader("Strict-Transport-Security",
        //     "max-age=31536000; includeSubDomains; preload");

        // Cache-Control para endpoints sensibles
        String uri = ((jakarta.servlet.http.HttpServletRequest) request).getRequestURI();
        if (uri.contains("/api/auth/") || uri.contains("/api/usuarios/")) {
            httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setHeader("Expires", "0");
        }

        chain.doFilter(request, response);
    }
}
