import { lazy, Suspense, useMemo } from "react";
import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { ConfigProvider, App as AntApp, Spin, theme as antTheme } from "antd";
import esES from "antd/locale/es_ES";
import { ProtectedRoute } from "@/components/auth/ProtectedRoute";
import { RoleGuard } from "@/components/auth/RoleGuard";
import { MainLayout } from "@/components/layout/MainLayout";
import { ErrorBoundary } from "@/components/common/ErrorBoundary";
import { useThemeStore } from "@/store/themeStore";

// Lazy load pages for better performance
const LoginPage = lazy(() => import("./pages/auth/LoginPage"));
const DashboardPage = lazy(() => import("./pages/dashboard/DashboardPage"));
const ClientesPage = lazy(() => import("./pages/clientes/ClientesPage"));
const PedidosPage = lazy(() => import("./pages/pedidos/PedidosPage"));
const ContenedoresPage = lazy(() => import("./pages/contenedores/ContenedoresPage"));
const ProductosPage = lazy(() => import("./pages/productos/ProductosPage"));
const InventarioPage = lazy(() => import("./pages/inventario/InventarioPage"));
const FacturasPage = lazy(() => import("./pages/facturas/FacturasPage"));
const ProveedoresPage = lazy(() => import("./pages/proveedores/ProveedoresPage"));
const UsuariosPage = lazy(() => import("./pages/usuarios/UsuariosPage"));
const ReportesPage = lazy(() => import("./pages/reportes/ReportesPage"));
const PerfilPage = lazy(() => import("./pages/perfil/PerfilPage"));
const ConfiguracionPage = lazy(() => import("./pages/configuracion/ConfiguracionPage"));
const NotFound = lazy(() => import("./pages/NotFound"));

// Loading fallback component
const PageLoader = () => (
  <div className="flex flex-col items-center justify-center min-h-screen gap-4">
    <Spin size="large" />
    <span>Cargando...</span>
  </div>
);

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5, // 5 minutes
      gcTime: 1000 * 60 * 10, // 10 minutes
      refetchOnWindowFocus: true,
      retry: 1,
    },
  },
});

const App = () => {
  const effectiveTheme = useThemeStore((state) => state.effectiveTheme);

  // Ant Design theme configuration with dark mode support
  const themeConfig = useMemo(() => ({
    algorithm: effectiveTheme === 'dark' ? antTheme.darkAlgorithm : antTheme.defaultAlgorithm,
    token: {
      colorPrimary: '#0066CC',
      colorSuccess: '#52c41a',
      colorWarning: '#faad14',
      colorError: '#f5222d',
      colorInfo: '#1890ff',
      borderRadius: 6,
      fontFamily: 'Inter, system-ui, sans-serif',
    },
  }), [effectiveTheme]);

  return (
    <ErrorBoundary>
      <QueryClientProvider client={queryClient}>
        <ConfigProvider locale={esES} theme={themeConfig}>
        <AntApp>
          <TooltipProvider>
            <Toaster />
            <Sonner />
            <BrowserRouter
              future={{
                v7_startTransition: true,
                v7_relativeSplatPath: true,
              }}
            >
              <Suspense fallback={<PageLoader />}>
                <Routes>
                  {/* Public routes */}
                  <Route path="/login" element={<LoginPage />} />
                
                {/* Protected routes */}
                <Route
                  path="/"
                  element={
                    <ProtectedRoute>
                      <MainLayout />
                    </ProtectedRoute>
                  }
                >
                  <Route index element={<Navigate to="/dashboard" replace />} />
                  <Route path="dashboard" element={<DashboardPage />} />
                  <Route path="perfil" element={<PerfilPage />} />
                  <Route path="configuracion" element={<ConfiguracionPage />} />

                  <Route
                    path="clientes"
                    element={
                      <RoleGuard allowedRoles={['ADMIN', 'OPERADOR', 'FINANZAS', 'CLIENTE']}>
                        <ClientesPage />
                      </RoleGuard>
                    }
                  />
                  
                  <Route
                    path="pedidos"
                    element={
                      <RoleGuard allowedRoles={['ADMIN', 'OPERADOR', 'DEPOSITO', 'CLIENTE']}>
                        <PedidosPage />
                      </RoleGuard>
                    }
                  />

                  <Route
                    path="contenedores"
                    element={
                      <RoleGuard allowedRoles={['ADMIN', 'OPERADOR', 'DEPOSITO']}>
                        <ContenedoresPage />
                      </RoleGuard>
                    }
                  />

                  <Route
                    path="productos"
                    element={
                      <RoleGuard allowedRoles={['ADMIN', 'OPERADOR', 'DEPOSITO']}>
                        <ProductosPage />
                      </RoleGuard>
                    }
                  />

                  <Route
                    path="inventario"
                    element={
                      <RoleGuard allowedRoles={['ADMIN', 'OPERADOR', 'DEPOSITO']}>
                        <InventarioPage />
                      </RoleGuard>
                    }
                  />

                  <Route
                    path="facturas"
                    element={
                      <RoleGuard allowedRoles={['ADMIN', 'FINANZAS', 'CLIENTE']}>
                        <FacturasPage />
                      </RoleGuard>
                    }
                  />

                  <Route
                    path="proveedores"
                    element={
                      <RoleGuard allowedRoles={['ADMIN', 'OPERADOR', 'FINANZAS']}>
                        <ProveedoresPage />
                      </RoleGuard>
                    }
                  />

                  <Route
                    path="usuarios"
                    element={
                      <RoleGuard allowedRoles={['ADMIN']}>
                        <UsuariosPage />
                      </RoleGuard>
                    }
                  />

                  <Route
                    path="reportes"
                    element={
                      <RoleGuard allowedRoles={['ADMIN', 'FINANZAS', 'OPERADOR']}>
                        <ReportesPage />
                      </RoleGuard>
                    }
                  />
                </Route>

                  {/* 404 Not Found */}
                  <Route path="*" element={<NotFound />} />
                </Routes>
              </Suspense>
            </BrowserRouter>
          </TooltipProvider>
        </AntApp>
      </ConfigProvider>
        <ReactQueryDevtools initialIsOpen={false} />
      </QueryClientProvider>
    </ErrorBoundary>
  );
};

export default App;
