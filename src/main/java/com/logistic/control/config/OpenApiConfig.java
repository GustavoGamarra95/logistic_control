package com.logistic.control.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        // Esquema de seguridad JWT
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Gestión Logística - API REST")
                        .description("""
                                API REST para gestión logística con integración SIFEN (Sistema de Facturación Electrónica Nacional de Paraguay).
                                
                                ## Características principales:
                                - Gestión de clientes, proveedores y productos
                                - Control de pedidos y containers
                                - Gestión de inventario/depósito
                                - Facturación electrónica (SIFEN)
                                - Generación de documentos electrónicos (DE)
                                - Firma digital XAdES-BES
                                - Generación de códigos QR (KuDE)
                                - Reportes en PDF
                                - Seguridad con JWT
                                - Encriptación de datos sensibles
                                
                                ## Roles y Permisos:
                                El sistema implementa 5 roles con diferentes niveles de acceso:
                                
                                ### ADMIN
                                - Acceso total al sistema
                                - Gestión de usuarios
                                - Acceso a actuator endpoints
                                - Todas las operaciones CRUD
                                
                                ### OPERADOR
                                - Gestión de clientes y proveedores
                                - Gestión de productos y pedidos
                                - Control de containers
                                - Gestión de inventario
                                
                                ### CLIENTE
                                - Consulta de sus propios pedidos
                                - Consulta de sus facturas
                                - Acceso limitado solo a sus datos
                                
                                ### FINANZAS
                                - Gestión completa de facturas
                                - Generación de reportes financieros
                                - Acceso a módulo de facturación SIFEN
                                
                                ### DEPOSITO
                                - Gestión de inventario y stock
                                - Control de movimientos de almacén
                                - Registro de entradas/salidas
                                
                                ## Autenticación:
                                Use el endpoint `/api/auth/login` para obtener el token JWT.
                                Luego incluya el token en el header: `Authorization: Bearer {token}`
                                
                                ## Usuarios de Prueba:
                                Puede usar estos usuarios para probar cada rol (password: **demo123** para todos):
                                
                                | Username   | Rol       | Permisos                                          |
                                |------------|-----------|---------------------------------------------------|
                                | admin      | ADMIN     | Acceso total, gestión usuarios, actuator        |
                                | operador   | OPERADOR  | Clientes, productos, pedidos, containers         |
                                | cliente1   | CLIENTE   | Solo consulta sus pedidos y facturas             |
                                | finanzas   | FINANZAS  | Gestión completa de facturas y SIFEN             |
                                | deposito   | DEPOSITO  | Gestión de inventario y movimientos              |
                                
                                ### Ejemplo de Login:
                                ```json
                                POST /api/auth/login
                                {
                                  "username": "admin",
                                  "password": "demo123"
                                }
                                ```
                                
                                Luego use el `accessToken` en el botón **Authorize** (🔓) arriba.
                                """)
                        .version(appVersion)
                        .contact(new Contact()
                                .name("Equipo de Desarrollo Logística")
                                .email("desarrollo@logistica.com.py")
                                .url("https://logistica.com.py"))
                        .license(new License()
                                .name("Propietario")
                                .url("https://logistica.com.py/license")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + contextPath)
                                .description("Servidor Local"),
                        new Server()
                                .url("https://api.logistica.com.py" + contextPath)
                                .description("Servidor Producción")
                ))
                // Agrupar endpoints por tags para mejorar la navegación en Swagger UI
                .tags(List.of(
                        new Tag().name("Autenticación").description("Endpoints para login, register y gestión de tokens JWT"),
                        new Tag().name("Clientes").description("Operaciones CRUD sobre clientes"),
                        new Tag().name("Pedidos").description("Gestión de pedidos y estados de envío"),
                        new Tag().name("Productos").description("Gestión de productos y stock"),
                        new Tag().name("Containers").description("Operaciones sobre containers y consolidación"),
                        new Tag().name("Facturación").description("Endpoints relacionados con SIFEN y DE (Documentos Electrónicos)")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingrese el token JWT obtenido del endpoint /api/auth/login. En Swagger UI haga clic en 'Authorize' y pegue: Bearer {token}")
                                )
                );
    }
}
