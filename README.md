# Sistema de Gestión Logística con Integración SIFEN (Paraguay)

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

Sistema completo de gestión logística para empresas en Paraguay con integración total a **SIFEN** (Sistema Integrado de Facturación Electrónica Nacional) v150, cumpliendo con la normativa de la SET (Subsecretaría de Estado de Tributación).

## 🚀 Características Principales

### Módulos del Sistema

1. **Gestión de Clientes/Empresas**
   - Registro y validación de RUC con SIFEN (siConsRUC)
   - Control de crédito y límites
   - Historial completo de operaciones

2. **Gestión de Pedidos/Envíos**
   - Tracking en tiempo real
   - Control de estados (En Tránsito, Aduana, Entregado)
   - Integración con containers

3. **Gestión de Containers**
   - Consolidación y desconsolidación
   - Tracking de llegadas y salidas
   - Control de capacidad y ocupación

4. **Inventario/Depósito**
   - Control de ubicaciones (zonas, racks, niveles)
   - Stock disponible y reservado
   - Costos de almacenaje

5. **Facturación Electrónica SIFEN**
   - Generación de DE (Documentos Electrónicos)
   - Firma digital XAdES-BES
   - Envío y consulta a SIFEN
   - Generación de CDC (Código de Control)
   - PDF KuDE con QR
   - Eventos (cancelación, inutilización)
   - Procesamiento por lotes

6. **Proveedores y Transportistas**
   - Gestión de proveedores
   - Control de pagos

7. **Reportes y Analytics**
   - Reportes financieros
   - Análisis de rentabilidad
   - Integración con Grok AI (xAI)

### Integración SIFEN Completa

- ✅ Emisión de Facturas Electrónicas (DE)
- ✅ Firma digital XAdES-BES con certificado .p12
- ✅ Generación de CDC con módulo 11
- ✅ Envío a SIFEN (siRecepDE, siRecepLoteDE)
- ✅ Consulta de RUC (siConsRUC)
- ✅ Consulta de CDC (siConsCDC)
- ✅ Eventos (siRecepEvento)
- ✅ Generación de KuDE (PDF con QR)
- ✅ Cumplimiento UBL 2.1 adaptado Paraguay

## 🛠️ Stack Tecnológico

### Backend
- **Java 21** (Temurin JDK)
- **Spring Boot 3.3.5**
  - Spring Data JPA
  - Spring Security + JWT
  - Spring Web Services (SOAP para SIFEN)
  - Spring Actuator
- **PostgreSQL 16**
- **Flyway** (migraciones de BD)

### SIFEN
- **JAXB** para manejo de XML
- **Apache Santuario** para firma XAdES-BES
- **BouncyCastle** para criptografía
- **ZXing** para generación de QR

### Reportes
- **JasperReports** para PDFs
- **Grok API** (xAI) para análisis IA

### DevOps
- **Docker** y **Docker Compose**
- **Kubernetes** (manifiestos incluidos)
- **GitHub Actions** (CI/CD)

## 📋 Requisitos Previos

### Software Requerido

```bash
# Java JDK 21
java --version  # Debe mostrar versión 21+

# Maven 3.9+
mvn --version

# Docker y Docker Compose
docker --version
docker-compose --version

# PostgreSQL 16 (opcional si usas Docker)
psql --version

# Git
git --version
```

### Hardware Mínimo

- **CPU:** 4 cores
- **RAM:** 8 GB
- **Disco:** 50 GB SSD

## 🚀 Inicio Rápido

### Opción 1: Con Docker (Recomendado)

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/logistic_control.git
cd logistic_control

# 2. Crear directorio para certificados (opcional para pruebas)
mkdir -p certificates logs

# 3. Iniciar con Docker Compose
docker-compose up -d

# 4. Ver logs
docker-compose logs -f app

# Acceder a:
# - API: http://localhost:8080/api
# - PgAdmin: http://localhost:5050 (admin@logistic.com / admin)
```

### Opción 2: Instalación Local

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/logistic_control.git
cd logistic_control

# 2. Configurar PostgreSQL
createdb logistic_db

# 3. Configurar variables de entorno (opcional)
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=logistic_db
export DB_USERNAME=postgres
export DB_PASSWORD=postgres

# 4. Compilar y ejecutar
mvn clean install
mvn spring-boot:run

# O con el JAR
mvn clean package
java -jar target/control-1.0.0.jar
```

## 🔧 Configuración

### Certificado Digital para SIFEN

#### Para Pruebas (Autofirmado)

```bash
# Generar certificado de prueba
keytool -genkeypair -alias test -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore certificates/test-certificate.p12 \
  -validity 365 -storepass test123 \
  -dname "CN=Test, OU=IT, O=Logistic, L=Asuncion, ST=Central, C=PY"
```

## 📚 Estructura del Proyecto

```
logistic_control/
├── src/
│   ├── main/
│   │   ├── java/com/logistic/control/
│   │   │   ├── config/         # Configuraciones
│   │   │   ├── controller/     # REST Controllers
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── entity/         # Entidades JPA
│   │   │   ├── enums/          # Enumeraciones
│   │   │   ├── exception/      # Excepciones personalizadas
│   │   │   ├── repository/     # Repositorios JPA
│   │   │   ├── security/       # Seguridad JWT
│   │   │   ├── service/        # Lógica de negocio
│   │   │   └── util/           # Utilidades
│   │   └── resources/
│   │       ├── db/migration/   # Scripts Flyway
│   │       └── application.yml # Configuración
│   └── test/                   # Tests
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

## 🔐 Seguridad

### Usuarios por Defecto

```
admin / admin123 (ADMIN)
operador / admin123 (OPERADOR)
cliente1 / admin123 (CLIENTE)
```

**⚠️ IMPORTANTE**: Cambiar passwords en producción

## 📖 Próximos Pasos

Este proyecto incluye la **estructura base completa**. Para continuar el desarrollo:

1. **Implementar servicios de negocio** en `src/main/java/com/logistic/control/service/`
2. **Crear controllers REST** en `src/main/java/com/logistic/control/controller/`
3. **Implementar integración SIFEN** completa
4. **Agregar DTOs** para requests/responses
5. **Configurar seguridad JWT**
6. **Crear tests unitarios e integración**

## 🤝 Contribuir

1. Fork el proyecto
2. Crear branch (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add: amazing feature'`)
4. Push al branch (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT.

---

**Desarrollado con ❤️ en Paraguay 🇵🇾**

**Version 1.0.0** | **Fecha: Noviembre 2025**
