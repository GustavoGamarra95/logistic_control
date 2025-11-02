# Guía de Desarrollo - Sistema de Gestión Logística SIFEN

## 📦 ¿Qué se ha generado?

### ✅ Completado (Estructura Base)

1. **Configuración del Proyecto**
   - ✅ `pom.xml` con todas las dependencias necesarias
   - ✅ `application.yml` con configuraciones completas
   - ✅ Estructura de directorios Maven estándar

2. **Modelo de Datos (30 archivos Java)**
   - ✅ 7 Enumeraciones (`enums/`)
     - TipoServicio, EstadoPedido, TipoContainer
     - EstadoInventario, EstadoFactura, Role, TipoProveedor, TipoCarga

   - ✅ 11 Entidades JPA (`entity/`)
     - BaseEntity (auditoría)
     - Cliente, Producto, Pedido, HistorialEstado
     - Container, Inventario
     - Factura, DetalleFactura, Pago
     - Proveedor, FacturaProveedor, Usuario

   - ✅ 7 Repositorios JPA (`repository/`)
     - ClienteRepository, PedidoRepository, ContainerRepository
     - InventarioRepository, FacturaRepository, ProductoRepository
     - ProveedorRepository, UsuarioRepository

   - ✅ Clase Principal
     - LogisticControlApplication.java

3. **Base de Datos**
   - ✅ Script Flyway inicial (V1__Initial_Schema.sql)
   - ✅ Datos de prueba (V2__Insert_Demo_Data.sql)
   - ✅ Esquema completo con todas las tablas

4. **DevOps**
   - ✅ Dockerfile multi-stage
   - ✅ docker-compose.yml (app + PostgreSQL + PgAdmin)
   - ✅ .gitignore configurado

5. **Documentación**
   - ✅ README.md completo
   - ✅ Este archivo (GUIA_DESARROLLO.md)

## 🚧 Pendiente de Implementar

### Fase 1: Seguridad JWT (Alta Prioridad)

**Archivos a crear en `src/main/java/com/logistic/control/security/`:**

```java
// JwtUtils.java - Generación y validación de tokens JWT
// JwtAuthenticationFilter.java - Filtro para validar tokens
// CustomUserDetailsService.java - Cargar usuarios desde BD
// SecurityConfig.java - Configuración Spring Security
// AuthController.java - Login y registro
```

**Dependencias ya incluidas:**
- io.jsonwebtoken:jjwt-api:0.12.6
- Spring Security

**Referencias:**
- JWT Secret en `application.yml`: `jwt.secret`
- Roles definidos en `enums/Role.java`
- Usuario implementa `UserDetails`

### Fase 2: DTOs (Data Transfer Objects)

**Crear en `src/main/java/com/logistic/control/dto/`:**

```
dto/
├── request/
│   ├── LoginRequest.java
│   ├── ClienteCreateRequest.java
│   ├── PedidoCreateRequest.java
│   ├── FacturaCreateRequest.java
│   └── ...
├── response/
│   ├── AuthResponse.java
│   ├── ClienteResponse.java
│   ├── PedidoResponse.java
│   ├── FacturaResponse.java
│   ├── ErrorResponse.java
│   └── ...
└── mapper/
    ├── ClienteMapper.java
    ├── PedidoMapper.java
    └── ...
```

**Recomendación:** Usar MapStruct para mapeo automático
```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
```

### Fase 3: Servicios de Negocio

**Crear en `src/main/java/com/logistic/control/service/`:**

```java
// ClienteService.java + ClienteServiceImpl.java
public interface ClienteService {
    ClienteResponse crear(ClienteCreateRequest request);
    ClienteResponse obtenerPorId(Long id);
    List<ClienteResponse> listarTodos();
    ClienteResponse actualizar(Long id, ClienteUpdateRequest request);
    void eliminar(Long id);
    ClienteResponse validarRucConSifen(String ruc);
}

// PedidoService.java + PedidoServiceImpl.java
// ContainerService.java + ContainerServiceImpl.java
// InventarioService.java + InventarioServiceImpl.java
// ProveedorService.java + ProveedorServiceImpl.java
```

### Fase 4: Integración SIFEN (Crítico)

**Crear en `src/main/java/com/logistic/control/service/sifen/`:**

```java
// SifenService.java - Servicio principal
public interface SifenService {
    // Emisión de DE
    FacturaResponse emitirDocumentoElectronico(Long facturaId);

    // Consultas
    ConsultaRucResponse consultarRuc(String ruc);
    ConsultaCdcResponse consultarCdc(String cdc);

    // Eventos
    void cancelarDocumento(String cdc, String motivo);
    void inutilizarDocumento(String cdc);

    // Lotes
    LoteResponse enviarLote(List<Long> facturasIds);
    LoteResultResponse consultarLote(String loteId);
}

// SifenXmlBuilder.java - Construcción XML UBL 2.1
// SifenSignatureService.java - Firma XAdES-BES
// SifenSoapClient.java - Cliente SOAP
// CdcGenerator.java - Generación CDC módulo 11
// QrCodeService.java - Generación QR para KuDE
```

**Referencias clave:**
- Manual SIFEN v150 (documentación adjunta)
- Guía UBL 2.1 (documentación adjunta)
- Endpoints SIFEN en `application.yml` (sifen.endpoints)

**Ejemplo estructura XML DE:**
```xml
<rDE xmlns="http://ekuatia.set.gov.py/sifen/xsd">
  <DE>
    <dVerFor>150</dVerFor>
    <gTimb>...</gTimb>
    <gDatGralOpe>...</gDatGralOpe>
    <gEmis>...</gEmis>
    <gDatRec>...</gDatRec>
    <gDtipDE>...</gDtipDE>
    <gTotSub>...</gTotSub>
  </DE>
</rDE>
```

### Fase 5: Controladores REST

**Crear en `src/main/java/com/logistic/control/controller/`:**

```java
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    @PostMapping
    ResponseEntity<ClienteResponse> crear(@Valid @RequestBody ClienteCreateRequest request);

    @GetMapping("/{id}")
    ResponseEntity<ClienteResponse> obtener(@PathVariable Long id);

    @GetMapping
    ResponseEntity<Page<ClienteResponse>> listar(@RequestParam(defaultValue = "0") int page);

    @PutMapping("/{id}")
    ResponseEntity<ClienteResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ClienteUpdateRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> eliminar(@PathVariable Long id);
}

// Otros controllers:
// - PedidoController
// - ContainerController
// - InventarioController
// - FacturaController
// - SifenController (endpoints SIFEN)
// - ProveedorController
// - ReporteController
```

### Fase 6: Manejo de Excepciones

**Crear en `src/main/java/com/logistic/control/exception/`:**

```java
// Excepciones personalizadas
public class EntityNotFoundException extends RuntimeException
public class ValidationException extends RuntimeException
public class SifenException extends RuntimeException
public class BusinessException extends RuntimeException

// Manejador global
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex);

    @ExceptionHandler(ValidationException.class)
    ResponseEntity<ErrorResponse> handleValidation(ValidationException ex);

    @ExceptionHandler(SifenException.class)
    ResponseEntity<ErrorResponse> handleSifen(SifenException ex);
}
```

### Fase 7: Configuraciones

**Crear en `src/main/java/com/logistic/control/config/`:**

```java
// WebConfig.java - CORS, Jackson config
// SifenConfig.java - Beans para SIFEN (JAXB, WS)
// GrokConfig.java - Cliente RestTemplate para Grok API
// JasperConfig.java - Configuración JasperReports
// AsyncConfig.java - Tareas asíncronas
```

### Fase 8: Reportes y Analytics

**Crear en `src/main/java/com/logistic/control/service/`:**

```java
// ReporteService.java
public interface ReporteService {
    List<ContainerResponse> containersEnTransito();
    List<ContainerResponse> containersEnAduana();
    RentabilidadResponse rentabilidadPorCliente(Long clienteId);
    ReporteFinancieroResponse reporteFinanciero(LocalDate inicio, LocalDate fin);
    String analizarConGrok(String pregunta); // Integración xAI
}

// GrokService.java - Cliente para Grok API
```

### Fase 9: Tests

**Crear en `src/test/java/com/logistic/control/`:**

```java
// Tests unitarios
@Test
public class ClienteServiceTest
@Test
public class SifenServiceTest
@Test
public class CdcGeneratorTest

// Tests de integración
@SpringBootTest
@Testcontainers
public class ClienteIntegrationTest

// Tests de controllers
@WebMvcTest(ClienteController.class)
public class ClienteControllerTest
```

### Fase 10: CI/CD

**Crear `.github/workflows/ci-cd.yml`:**

```yaml
name: CI/CD Pipeline
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
      - name: Build with Maven
        run: mvn clean install
      - name: Run tests
        run: mvn test
      - name: Build Docker image
        run: docker build -t logistic-control .
```

## 🎯 Orden de Implementación Recomendado

1. **Seguridad JWT** (sin esto no puedes proteger endpoints)
2. **DTOs y Mappers** (para comunicación API)
3. **Servicios de Negocio básicos** (Cliente, Pedido, Inventario)
4. **Controllers REST** (exponer API)
5. **Manejo de Excepciones** (para errores consistentes)
6. **Integración SIFEN** (funcionalidad crítica)
7. **Reportes y Grok**
8. **Tests**
9. **CI/CD y Kubernetes**

## 🚀 Cómo Ejecutar el Proyecto Actual

```bash
# 1. Con Docker (más fácil)
docker-compose up -d

# 2. Local (requiere PostgreSQL instalado)
# Crear base de datos
createdb logistic_db

# Ejecutar
mvn spring-boot:run

# Ver logs
tail -f logs/logistic-control.log
```

## 📊 Estado Actual

```
✅ Estructura del proyecto: 100%
✅ Modelo de datos (JPA): 100%
✅ Base de datos (Flyway): 100%
✅ Configuración (application.yml): 100%
✅ Docker/DevOps: 100%

⏳ Seguridad JWT: 0%
⏳ DTOs: 0%
⏳ Servicios de negocio: 0%
⏳ Controllers REST: 0%
⏳ Integración SIFEN: 0%
⏳ Reportes: 0%
⏳ Tests: 0%

Total completado: ~35%
```

## 📚 Recursos Útiles

### SIFEN
- Manual Técnico SIFEN v150
- Guía UBL 2.1
- https://www.set.gov.py/sifen
- Ambiente test: https://sifen-test.set.gov.py

### Spring Boot
- https://docs.spring.io/spring-boot/docs/3.3.5/reference/html/
- https://spring.io/guides

### JWT
- https://github.com/jwtk/jjwt

### Grok API (xAI)
- https://docs.x.ai/api

## 💡 Consejos

1. **Comienza con tests de endpoints simples** antes de integrar SIFEN
2. **Usa Postman o similar** para probar la API mientras desarrollas
3. **Implementa logging robusto** (ya configurado con Logback)
4. **Mantén documentación actualizada** con Swagger/OpenAPI
5. **Haz commits frecuentes** con mensajes descriptivos

## 🆘 Soporte

Si necesitas ayuda con alguna implementación específica:
1. Revisa los comentarios en el código
2. Consulta la documentación de SIFEN
3. Verifica los ejemplos en los repositorios de referencia (README.md)

---

¡Buena suerte con el desarrollo! 🚀
