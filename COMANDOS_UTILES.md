# Comandos Útiles - Sistema Logístico SIFEN

## 🐳 Docker

### Gestión de Contenedores

```bash
# Iniciar todos los servicios
docker-compose up -d

# Iniciar con rebuild (si cambiaste código)
docker-compose up -d --build

# Ver logs en tiempo real
docker-compose logs -f

# Ver logs solo de la aplicación
docker-compose logs -f app

# Ver logs de PostgreSQL
docker-compose logs -f postgres

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes (CUIDADO: borra la BD)
docker-compose down -v

# Ver estado de servicios
docker-compose ps

# Reiniciar solo la app
docker-compose restart app

# Entrar al contenedor de la app
docker-compose exec app sh

# Entrar a PostgreSQL
docker-compose exec postgres psql -U postgres -d logistic_db
```

### Docker sin Compose

```bash
# Build manual
docker build -t logistic-control:1.0.0 .

# Run manual
docker run -d -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  logistic-control:1.0.0

# Ver logs
docker logs -f <container-id>

# Eliminar imágenes antiguas
docker system prune -a
```

## 🔨 Maven

### Build y Compilación

```bash
# Compilar (sin tests)
mvn clean install -DskipTests

# Compilar con tests
mvn clean install

# Solo compilar
mvn compile

# Empaquetar JAR
mvn clean package

# Limpiar target/
mvn clean

# Ver árbol de dependencias
mvn dependency:tree

# Actualizar dependencias
mvn versions:display-dependency-updates

# Verificar formato de código
mvn spotless:check

# Aplicar formato
mvn spotless:apply
```

### Ejecución

```bash
# Ejecutar aplicación
mvn spring-boot:run

# Ejecutar en modo debug (puerto 5005)
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Ejecutar con profile específico
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Ejecutar JAR directamente
java -jar target/control-1.0.0.jar

# Con variables de entorno
DB_HOST=localhost DB_PORT=5432 mvn spring-boot:run
```

### Tests

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar test específico
mvn test -Dtest=ClienteServiceTest

# Tests con cobertura
mvn test jacoco:report

# Ver reporte de cobertura
open target/site/jacoco/index.html

# Tests de integración
mvn verify

# Solo tests unitarios
mvn surefire:test

# Solo tests de integración
mvn failsafe:integration-test
```

## 🗄️ PostgreSQL

### Conexión y Consultas

```bash
# Conectar a PostgreSQL (en Docker)
docker-compose exec postgres psql -U postgres -d logistic_db

# Conectar a PostgreSQL (local)
psql -h localhost -U postgres -d logistic_db

# Listar bases de datos
\l

# Conectar a base de datos
\c logistic_db

# Listar tablas
\dt

# Describir tabla
\d clientes

# Ver datos de tabla
SELECT * FROM clientes;

# Salir
\q
```

### Migraciones Flyway

```bash
# Ver estado de migraciones
mvn flyway:info

# Ejecutar migraciones pendientes
mvn flyway:migrate

# Reparar historial de migraciones
mvn flyway:repair

# Limpiar base de datos (CUIDADO: borra todo)
mvn flyway:clean

# Validar migraciones
mvn flyway:validate

# Baseline (para bases existentes)
mvn flyway:baseline
```

### Backups

```bash
# Backup completo
docker-compose exec postgres pg_dump -U postgres logistic_db > backup.sql

# Backup solo schema
docker-compose exec postgres pg_dump -U postgres -s logistic_db > schema.sql

# Backup solo datos
docker-compose exec postgres pg_dump -U postgres -a logistic_db > data.sql

# Restaurar backup
docker-compose exec -T postgres psql -U postgres logistic_db < backup.sql
```

## 🔍 Debugging

### Logs

```bash
# Ver logs de la aplicación
tail -f logs/logistic-control.log

# Ver logs con grep
tail -f logs/logistic-control.log | grep ERROR

# Limpiar logs
rm -rf logs/*.log
```

### JVM y Performance

```bash
# Ver procesos Java
jps -l

# Thread dump
jstack <pid>

# Heap dump
jmap -dump:format=b,file=heap.bin <pid>

# Ver uso de memoria
jstat -gc <pid> 1000
```

## 🧪 Testing con cURL

### Autenticación

```bash
# Login (obtener token)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# Guardar token en variable
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.token')
```

### Clientes

```bash
# Listar clientes
curl -X GET http://localhost:8080/api/clientes \
  -H "Authorization: Bearer $TOKEN"

# Crear cliente
curl -X POST http://localhost:8080/api/clientes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "razonSocial": "EMPRESA TEST S.A.",
    "ruc": "80099999-9",
    "email": "test@empresa.com.py",
    "direccion": "Asunción",
    "pais": "Paraguay",
    "tipoServicio": "MARITIMO"
  }'

# Obtener cliente por ID
curl -X GET http://localhost:8080/api/clientes/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Health Check

```bash
# Health check
curl http://localhost:8080/api/actuator/health

# Metrics
curl http://localhost:8080/api/actuator/metrics

# Info
curl http://localhost:8080/api/actuator/info
```

## 🔐 Certificados SIFEN

### Generar Certificado de Prueba

```bash
# Generar certificado .p12
keytool -genkeypair -alias test -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore certificates/test-certificate.p12 \
  -validity 365 -storepass test123 \
  -dname "CN=Test, OU=IT, O=Logistic, L=Asuncion, ST=Central, C=PY"

# Ver contenido del certificado
keytool -list -v -keystore certificates/test-certificate.p12 \
  -storepass test123
```

### Convertir Certificados

```bash
# PEM a PKCS12
openssl pkcs12 -export -in certificado.crt -inkey private.key \
  -out certificado.p12 -name "mi-certificado"

# PKCS12 a PEM
openssl pkcs12 -in certificado.p12 -out certificado.pem -nodes

# Ver info del certificado
openssl x509 -in certificado.crt -text -noout
```

## 📊 Monitoreo

### Actuator Endpoints

```bash
# Health
curl http://localhost:8080/api/actuator/health | jq

# Metrics
curl http://localhost:8080/api/actuator/metrics | jq

# Prometheus
curl http://localhost:8080/api/actuator/prometheus

# Ver todas las métricas disponibles
curl http://localhost:8080/api/actuator/metrics | jq '.names[]'

# Métrica específica
curl http://localhost:8080/api/actuator/metrics/jvm.memory.used | jq
```

## 🔧 Utilidades

### Git

```bash
# Estado del repositorio
git status

# Ver cambios
git diff

# Agregar todos los cambios
git add .

# Commit
git commit -m "feat: agregar nueva funcionalidad"

# Push
git push origin main

# Ver log
git log --oneline --graph
```

### Scripts útiles

```bash
# Limpiar todo y reiniciar
docker-compose down -v && \
mvn clean && \
docker-compose up -d --build

# Rebuild rápido
mvn clean package -DskipTests && \
docker-compose up -d --build app

# Ver puertos en uso
lsof -i :8080
lsof -i :5432

# Matar proceso en puerto
kill -9 $(lsof -t -i:8080)
```

## 📦 Instalación de Dependencias

```bash
# Instalar OpenJDK 21 (Ubuntu/Debian)
sudo apt update
sudo apt install openjdk-21-jdk

# Instalar Maven
sudo apt install maven

# Instalar Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Instalar Docker Compose
sudo apt install docker-compose-plugin

# Verificar instalaciones
java --version
mvn --version
docker --version
docker-compose --version
```

## 🚀 Deployment

### Kubernetes

```bash
# Aplicar manifiestos
kubectl apply -f k8s/

# Ver pods
kubectl get pods

# Ver logs
kubectl logs -f deployment/logistic-control

# Describir pod
kubectl describe pod <pod-name>

# Port forward
kubectl port-forward service/logistic-control 8080:8080

# Eliminar deployment
kubectl delete -f k8s/
```

### Variables de Entorno

```bash
# Crear archivo .env
cat > .env << 'EOF'
DB_HOST=localhost
DB_PORT=5432
DB_NAME=logistic_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
SIFEN_ENV=test
SIFEN_TIMBRADO=12345678
EOF

# Cargar variables
export $(cat .env | xargs)

# Ver variables
env | grep DB_
```

## 🎯 Troubleshooting

```bash
# Puerto 8080 ocupado
lsof -i :8080
kill -9 $(lsof -t -i:8080)

# PostgreSQL no arranca en Docker
docker-compose logs postgres
docker volume rm logistic_control_postgres_data

# Maven no encuentra dependencias
mvn dependency:purge-local-repository
mvn clean install -U

# Aplicación no arranca
# 1. Verificar logs
tail -f logs/logistic-control.log

# 2. Verificar BD está corriendo
docker-compose ps

# 3. Verificar conexión a BD
docker-compose exec postgres pg_isready

# 4. Reiniciar todo
docker-compose restart
```

---

💡 **Tip**: Guarda este archivo como referencia rápida durante el desarrollo.
