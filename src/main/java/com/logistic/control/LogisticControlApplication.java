package com.logistic.control;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Sistema de Gestión Logística con Integración SIFEN (Paraguay)
 * Version: 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class LogisticControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticControlApplication.class, args);
        System.out.println("""

            ========================================
            Sistema de Gestión Logística - SIFEN
            Version: 1.0.0
            Puerto: 8080
            ========================================
            """);
    }
}
