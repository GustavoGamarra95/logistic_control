# Sistema de Gestión Logística con Integración SIFEN (Paraguay)

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Java](https://img.shields.io/badge/Java-25-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

Proyecto backend Java Spring Boot para la gestión logística con integración completa a SIFEN (Sistema de Facturación Electrónica Nacional - Paraguay). Está pensado para entornos de prueba y producción, con autenticación JWT, cifrado de datos sensibles y migraciones con Flyway.

---

## Contenido de este README
- Requisitos
- Inicio rápido (Docker y local)
- Variables de entorno y `.env.example`
- Migraciones (Flyway)
- Documentación OpenAPI / Swagger
- Endpoints principales y ejemplos curl
- Seguridad y buenas prácticas
- Checklist de verificación antes de desplegar

---

## Requisitos
- Java 25+
- Maven 3.9+
- Docker & Docker Compose (recomendado)
- PostgreSQL 16 (si no usa Docker)
- Git

Compruebe versiones:

```bash
java --version
mvn --version
docker --version
docker-compose --version
```

---

## Inicio Rápido

### Opción A — Con Docker (recomendado para pruebas)

1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/logistic_control.git
cd logistic_control
```

2. Copiar ejemplo de variables y crear carpetas necesarias

```bash
cp .env.example .env
mkdir -p certificates logs
```

3. Levantar servicios

```bash
docker-compose up -d --build
```

4. Ver logs de la aplicación

```bash
docker-compose logs -f app
```

URLs importantes (por defecto):
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- OpenAPI (JSON): http://localhost:8080/api/api-docs
- PgAdmin (si está en docker-compose): http://localhost:5050


### Opción B — Ejecución local (sin Docker)

1. Configure la base de datos PostgreSQL y cree la BD (ejemplo):

```bash
createdb logistic_db
```

2. Exporte las variables de entorno necesarias (ver `.env.example`) o use su propio método de configuración.

3. Compilar y ejecutar:

```bash
mvn clean package -DskipTests=false
mvn spring-boot:run
# O con el jar
java -jar target/*.jar
```

---

## Variables de entorno (.env.example)

Cree un archivo `.env` en la raíz (no lo versiones). Ejemplo mínimo:

```
# Database
DB_HOST=postgres
DB_PORT=5432
DB_NAME=logistic_db
DB_USERNAME=postgres
DB_PASSWORD=postgres

# JWT
JWT_SECRET=your_jwt_secret_change_me
JWT_EXPIRATION_MS=86400000
REFRESH_TOKEN_EXPIRATION_MS=604800000

# Encryption
ENCRYPTION_KEY=base64_or_hex_encryption_key

# SIFEN (opcional)
SIFEN_CERT_PATH=certificates/your-cert.p12
SIFEN_CERT_PASSWORD=changeit

# App
SPRING_PROFILES_ACTIVE=dev
```

Notas:
- Nunca suba `.env` ni secretos al repositorio.
- En producción use un vault o secret manager.

---

## Migraciones (Flyway)

Los scripts SQL están en `src/main/resources/db/migration` y se ejecutan automáticamente al arrancar la aplicación si Flyway está habilitado.

Para ejecutar migraciones manualmente puede usar la imagen oficial de Flyway o dejar que la aplicación las ejecute al inicio.

Ejemplo usando la imagen Flyway:

```bash
docker run --rm \
  -v $(pwd)/src/main/resources/db/migration:/flyway/sql \
  flyway/flyway:9 -url=jdbc:postgresql://host:5432/logistic_db -user=postgres -password=postgres migrate
```

---

## Documentación OpenAPI / Swagger

La aplicación expone la documentación OpenAPI generada con springdoc. Si corre localmente: `http://localhost:8080/api/swagger-ui.html`.

Se ha configurado un SecurityScheme para JWT (Bearer). Para usarlo en Swagger UI:
1. Haga login vía `/api/auth/login`.
2. Copie el `accessToken` y en el botón "Authorize" pegue: `Bearer {token}`.

El `OpenApiConfig` agrupa endpoints en tags: Autenticación, Clientes, Pedidos, Productos, Containers y Facturación.

---

## Endpoints principales y ejemplos

### Autenticación

Registro:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nuevo_usuario",
    "password": "password123",
    "nombre": "Nombre",
    "apellido": "Apellido",
    "email": "email@example.com",
    "roles": ["CLIENTE"]
  }'
```

Login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"demo1234"}'
```

Refresh token:

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Authorization: Bearer <refresh_token>"
```

Obtener usuario actual:

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <access_token>"
```

Ejemplo de uso de API protegida:

```bash
curl -X GET http://localhost:8080/api/clientes \
  -H "Authorization: Bearer <access_token>"
```

---

## Seguridad

- JWT para autenticación (HS384 por defecto). Cambie `JWT_SECRET` en producción.
- BCrypt para passwords.
- Datos sensibles cifrados con AES-256-GCM.
- Swagger UI está disponible por defecto en entornos `dev`. Recomendado ocultarlo o protegerlo en `prod`.

---

## Desarrollo y pruebas

- Añadir tests: `mvn test`
- Recomendado usar Testcontainers para pruebas de integración con Postgres.

---

## Checklist antes de desplegar (mínimo)

- [ ] Actualizar `JWT_SECRET` y `ENCRYPTION_KEY` con valores seguros
- [ ] Validar certificados `.p12` para SIFEN y su contraseña
- [ ] Revisar `SPRING_PROFILES_ACTIVE` y configuración por entorno
- [ ] Ejecutar `mvn -DskipTests=false clean package` y resolver fallos
- [ ] Añadir/ejecutar pruebas unitarias e integración
- [ ] Revisar configuración de CORS y security headers
- [ ] Revisar que Swagger UI no esté expuesto públicamente en prod

---

## Contribuir

- Fork -> Branch -> PR

## Licencia

MIT

---

**Desarrollado con ❤️ en Paraguay 🇵🇾**

**Version 1.0.0** | **Fecha: Noviembre 2025**
