# LogiControl - Sistema de Control Logístico Enterprise

Sistema enterprise completo de control logístico desarrollado en React con TypeScript, Ant Design y React Query. Gestiona importaciones/exportaciones, contenedores, inventario, productos, proveedores y facturación electrónica SIFEN (Paraguay).

## 🚀 Stack Tecnológico

### Core
- **React 18+** con TypeScript
- **Vite** como bundler
- **React Router v6** para navegación

### UI/UX
- **Ant Design 5.x** - Componentes enterprise
- **Tailwind CSS** - Estilos customizados
- **Ant Design Icons** - Iconografía

### Data Management
- **React Query (TanStack Query v5)** para:
  - Fetching en tiempo real desde Spring Boot
  - Caching inteligente
  - Sincronización automática
  - Optimistic updates

### Forms & Validation
- **React Hook Form** - Formularios complejos
- **Zod** - Validación de schemas

### Charts & Visualization
- **Recharts** - Dashboards y métricas

### State Management
- **Zustand** - Estado global ligero

### Additional
- **Axios** - HTTP client con interceptors JWT
- **date-fns** - Manejo de fechas

## 📁 Estructura del Proyecto

```
src/
├── api/                    # API clients y configuración
│   ├── axios-config.ts     # Axios instance con JWT
│   ├── auth.api.ts         # Endpoints autenticación
│   └── clientes.api.ts     # Endpoints clientes
│
├── components/
│   ├── auth/               # Componentes de autenticación
│   │   ├── LoginForm.tsx
│   │   ├── ProtectedRoute.tsx
│   │   └── RoleGuard.tsx
│   │
│   └── layout/             # Layout principal
│       ├── MainLayout.tsx
│       ├── Header.tsx
│       └── Sidebar.tsx
│
├── hooks/                  # Custom hooks
│   ├── useAuth.ts          # Hook autenticación
│   ├── useClientes.ts      # React Query: clientes
│   ├── usePagination.ts
│   ├── useDebounce.ts
│   └── usePermissions.ts
│
├── pages/                  # Páginas de la aplicación
│   ├── auth/
│   │   └── LoginPage.tsx
│   ├── dashboard/
│   │   └── DashboardPage.tsx
│   └── clientes/
│       └── ClientesPage.tsx
│
├── store/                  # Zustand stores
│   ├── authStore.ts        # Estado de autenticación
│   └── uiStore.ts          # Estado UI (sidebar, theme)
│
├── types/                  # TypeScript types
│   ├── auth.types.ts
│   ├── cliente.types.ts
│   └── api.types.ts
│
├── utils/                  # Utilidades
│   ├── constants.ts
│   └── format.ts
│
└── App.tsx                 # Configuración principal
```

## 🔧 Instalación y Configuración

### Prerrequisitos
- Node.js 18+ y npm
- Backend Spring Boot corriendo en `http://localhost:8080`

### Pasos de instalación

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd logistic-frontend
```

2. **Instalar dependencias**
```bash
npm install
```

3. **Configurar variables de entorno**
```bash
cp .env.example .env
```

Editar `.env`:
```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_NAME=Sistema de Control Logístico
VITE_ENVIRONMENT=development
```

4. **Iniciar el servidor de desarrollo**
```bash
npm run dev
```

La aplicación estará disponible en `http://localhost:8080`

## 🔐 Sistema de Autenticación

### Roles del Sistema

- **ADMIN**: Acceso total al sistema
- **OPERADOR**: Clientes, pedidos, contenedores, productos, inventario
- **FINANZAS**: Facturación y reportes financieros
- **DEPOSITO**: Inventario y gestión de almacén
- **CLIENTE**: Ver propios pedidos, facturas e inventario

### Flujo de Autenticación

1. Login con usuario/email y contraseña
2. Backend retorna `accessToken` y `refreshToken`
3. Token se almacena en localStorage
4. Axios interceptor agrega token a todas las requests
5. Refresh automático cuando el token expira

## 📊 Módulos Principales

### ✅ Implementados (MVP)

- **Autenticación**: Login, logout, gestión de roles
- **Dashboard**: KPIs y métricas básicas
- **Clientes**: CRUD completo con paginación y búsqueda

### 🚧 En Desarrollo

- **Pedidos**: Gestión completa de pedidos con tracking
- **Contenedores**: Consolidación y ocupación
- **Productos**: Catálogo de productos
- **Inventario**: Entrada/salida/reserva
- **Facturas**: Integración SIFEN
- **Proveedores**: Gestión de proveedores
- **Usuarios**: Administración de usuarios

## 🎨 Design System

El proyecto utiliza un design system corporativo basado en:

- **Primary**: Azul corporativo (#0066CC)
- **Success**: Verde (#52c41a)
- **Warning**: Amarillo (#faad14)
- **Error**: Rojo (#f5222d)
- **Tipografía**: Inter (UI) / IBM Plex Sans (datos)

Todos los colores están definidos en `src/index.css` usando variables CSS HSL.

## 🔨 Scripts Disponibles

```bash
npm run dev          # Servidor de desarrollo
npm run build        # Build de producción
npm run preview      # Preview del build
npm run lint         # Ejecutar ESLint
```

## 🌐 API Backend

El frontend se conecta a un backend Spring Boot en `http://localhost:8080/api`

### Endpoints principales:

```
POST   /auth/login              # Login
POST   /auth/register           # Registro
GET    /auth/me                 # Usuario actual
POST   /auth/refresh            # Refresh token

GET    /clientes                # Lista de clientes
POST   /clientes                # Crear cliente
GET    /clientes/{id}           # Cliente por ID
PUT    /clientes/{id}           # Actualizar cliente
DELETE /clientes/{id}           # Eliminar cliente
PATCH  /clientes/{id}/credito   # Actualizar crédito
```

## 📝 Próximas Funcionalidades

1. **Fase 2 - Operaciones**
   - CRUD Productos
   - CRUD Contenedores
   - Consolidación de contenedores
   - CRUD Inventario

2. **Fase 3 - Facturación**
   - CRUD Facturas
   - Integración SIFEN
   - Generación de QR y KuDE

3. **Fase 4 - Analytics & Maps**
   - Dashboard completo con gráficos
   - Mapas con Mapbox (tracking, almacén, rutas)

4. **Fase 5 - Admin**
   - CRUD Proveedores
   - CRUD Usuarios
   - Gestión de roles

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es privado y confidencial.

## 👥 Equipo

Desarrollado para gestión logística enterprise con integración SIFEN Paraguay.
