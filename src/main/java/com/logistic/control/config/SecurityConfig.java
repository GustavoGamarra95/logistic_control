package com.logistic.control.config;

import com.logistic.control.security.JwtAuthenticationFilter;
import com.logistic.control.security.RateLimitFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuración de seguridad con JWT
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final RateLimitFilter rateLimitFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    // Controla si Swagger/OpenAPI UI está habilitado (por defecto true)
    @Value("${springdoc.swagger-ui.enabled:true}")
    private boolean swaggerEnabled;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                         RateLimitFilter rateLimitFilter,
                         CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.rateLimitFilter = rateLimitFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        http
                // Configurar CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                

                .csrf(AbstractHttpConfigurer::disable)
                
                // Configurar autorización
                .authorizeHttpRequests(auth -> {
                        // Endpoints públicos (sin /api porque ya está en context-path)
                        auth.requestMatchers("/auth/**").permitAll();
                        auth.requestMatchers("/actuator/health").permitAll();
                        auth.requestMatchers("/actuator/info").permitAll();

                        // Swagger UI y OpenAPI docs: comportamiento condicionado por propiedad
                        auth.requestMatchers("/webjars/**").permitAll();
                        auth.requestMatchers("/swagger-resources/**").permitAll();
                        if (swaggerEnabled) {
                            auth.requestMatchers("/v3/api-docs/**", "/api-docs/**").permitAll();
                            auth.requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll();
                        } else {
                            // En producción o cuando está deshabilitado, restringir acceso a administradores
                            auth.requestMatchers("/v3/api-docs/**", "/api-docs/**").hasRole("ADMIN");
                            auth.requestMatchers("/swagger-ui/**", "/swagger-ui.html").hasRole("ADMIN");
                        }

                        // Endpoints protegidos por rol (sin /api porque ya está en context-path)
                        auth.requestMatchers("/clientes/**").hasAnyRole("ADMIN", "OPERADOR");
                        auth.requestMatchers("/productos/**").hasAnyRole("ADMIN", "OPERADOR");
                        auth.requestMatchers("/pedidos/**").hasAnyRole("ADMIN", "OPERADOR", "CLIENTE");
                        auth.requestMatchers("/containers/**").hasAnyRole("ADMIN", "OPERADOR");
                        auth.requestMatchers("/inventario/**").hasAnyRole("ADMIN", "OPERADOR", "DEPOSITO");
                        auth.requestMatchers("/facturas/**").hasAnyRole("ADMIN", "FINANZAS");
                        auth.requestMatchers("/proveedores/**").hasAnyRole("ADMIN", "OPERADOR");
                        auth.requestMatchers("/usuarios/**").hasRole("ADMIN");

                        // Endpoints de testing (requieren autenticación pero validan roles internamente)
                        auth.requestMatchers("/test/**").authenticated();

                        // Actuator endpoints solo para ADMIN
                        auth.requestMatchers("/actuator/**").hasRole("ADMIN");

                        // Todo lo demás requiere autenticación
                        auth.anyRequest().authenticated();
                })

                // Configurar gestión de sesiones (stateless para JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true)
                )
                
                // Agregar authentication provider
                .authenticationProvider(authenticationProvider)
                
                // Agregar filtros en orden correcto
                .addFilterBefore(rateLimitFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, 
                                                         PasswordEncoder passwordEncoder) {
        // Usando configuración moderna de DaoAuthenticationProvider
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt con strength 12 (más seguro que el default 10)
        // Balance entre seguridad y performance
        return new BCryptPasswordEncoder(12);
    }
}
