package com.logistic.control.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller de prueba para verificar roles y permisos
 */
@RestController
@RequestMapping("/test")
@Tag(name = "🧪 Testing", description = "Endpoints de prueba para verificar roles y permisos")
public class TestController {

    @GetMapping("/whoami")
    @Operation(
        summary = "¿Quién soy?",
        description = "Retorna información sobre el usuario autenticado actual, sus roles y permisos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Información del usuario"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, Object>> whoami() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> info = new HashMap<>();
        info.put("username", auth.getName());
        info.put("roles", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        info.put("authenticated", auth.isAuthenticated());
        info.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(info);
    }

    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "🔒 Solo ADMIN",
        description = "Endpoint accesible solo para usuarios con rol ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Acceso concedido - Eres ADMIN"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - No tienes rol ADMIN")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<TestResponse> adminOnly() {
        return ResponseEntity.ok(TestResponse.builder()
                .message("✅ Acceso concedido - Tienes rol ADMIN")
                .role("ADMIN")
                .access("TOTAL")
                .permissions("Gestión de usuarios, actuator, todas las operaciones")
                .build());
    }

    @GetMapping("/operador-only")
    @PreAuthorize("hasRole('OPERADOR')")
    @Operation(
        summary = "🔒 Solo OPERADOR",
        description = "Endpoint accesible solo para usuarios con rol OPERADOR"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Acceso concedido - Eres OPERADOR"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - No tienes rol OPERADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<TestResponse> operadorOnly() {
        return ResponseEntity.ok(TestResponse.builder()
                .message("✅ Acceso concedido - Tienes rol OPERADOR")
                .role("OPERADOR")
                .access("OPERACIONES")
                .permissions("Clientes, productos, pedidos, containers, inventario, proveedores")
                .build());
    }

    @GetMapping("/cliente-only")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(
        summary = "🔒 Solo CLIENTE",
        description = "Endpoint accesible solo para usuarios con rol CLIENTE"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Acceso concedido - Eres CLIENTE"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - No tienes rol CLIENTE")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<TestResponse> clienteOnly() {
        return ResponseEntity.ok(TestResponse.builder()
                .message("✅ Acceso concedido - Tienes rol CLIENTE")
                .role("CLIENTE")
                .access("LIMITADO")
                .permissions("Consulta de tus pedidos y facturas")
                .build());
    }

    @GetMapping("/finanzas-only")
    @PreAuthorize("hasRole('FINANZAS')")
    @Operation(
        summary = "🔒 Solo FINANZAS",
        description = "Endpoint accesible solo para usuarios con rol FINANZAS"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Acceso concedido - Eres FINANZAS"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - No tienes rol FINANZAS")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<TestResponse> finanzasOnly() {
        return ResponseEntity.ok(TestResponse.builder()
                .message("✅ Acceso concedido - Tienes rol FINANZAS")
                .role("FINANZAS")
                .access("FINANCIERO")
                .permissions("Gestión de facturas, SIFEN, reportes financieros")
                .build());
    }

    @GetMapping("/deposito-only")
    @PreAuthorize("hasRole('DEPOSITO')")
    @Operation(
        summary = "🔒 Solo DEPOSITO",
        description = "Endpoint accesible solo para usuarios con rol DEPOSITO"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Acceso concedido - Eres DEPOSITO"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - No tienes rol DEPOSITO")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<TestResponse> depositoOnly() {
        return ResponseEntity.ok(TestResponse.builder()
                .message("✅ Acceso concedido - Tienes rol DEPOSITO")
                .role("DEPOSITO")
                .access("ALMACEN")
                .permissions("Gestión de inventario, movimientos de almacén")
                .build());
    }

    @GetMapping("/admin-or-operador")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    @Operation(
        summary = "🔒 ADMIN o OPERADOR",
        description = "Endpoint accesible para ADMIN o OPERADOR (ejemplo de múltiples roles)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Acceso concedido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Necesitas rol ADMIN o OPERADOR")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<TestResponse> adminOrOperador() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));
        
        return ResponseEntity.ok(TestResponse.builder()
                .message("✅ Acceso concedido - Tienes permisos administrativos u operativos")
                .role(roles)
                .access("GESTION")
                .permissions("Operaciones de gestión del sistema")
                .build());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class TestResponse {
        private String message;
        private String role;
        private String access;
        private String permissions;
    }
}
