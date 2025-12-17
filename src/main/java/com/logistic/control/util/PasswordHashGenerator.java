package com.logistic.control.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String password = "demo1234";
        String hash = encoder.encode(password);

        System.out.println("=".repeat(60));
        System.out.println("Hash BCrypt (strength 12) para 'demo1234':");
        System.out.println(hash);
        System.out.println("=".repeat(60));
        
        // Verificar
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verificación: " + (matches ? "✓ OK" : "✗ FAILED"));
        System.out.println("=".repeat(60));
    }
}
