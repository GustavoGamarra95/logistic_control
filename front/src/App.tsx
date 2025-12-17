import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { ConfigProvider, App as AntApp } from "antd";
import esES from "antd/locale/es_ES";
import { ProtectedRoute } from "@/components/auth/ProtectedRoute";
import { RoleGuard } from "@/components/auth/RoleGuard";
import { MainLayout } from "@/components/layout/MainLayout";
import { ErrorBoundary } from "@/components/common/ErrorBoundary";
import LoginPage from "./pages/auth/LoginPage";
import DashboardPage from "./pages/dashboard/DashboardPage";
import ClientesPage from "./pages/clientes/ClientesPage";
import PedidosPage from "./pages/pedidos/PedidosPage";
import ContenedoresPage from "./pages/contenedores/ContenedoresPage";
import ProductosPage from "./pages/productos/ProductosPage";
import InventarioPage from "./pages/inventario/InventarioPage";
import FacturasPage from "./pages/facturas/FacturasPage";
import ProveedoresPage from "./pages/proveedores/ProveedoresPage";
import UsuariosPage from "./pages/usuarios/UsuariosPage";
import NotFound from "./pages/NotFound";

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

// Ant Design theme configuration
const theme = {
  token: {
    colorPrimary: '#0066CC',
    colorSuccess: '#52c41a',
    colorWarning: '#faad14',
    colorError: '#f5222d',
    colorInfo: '#1890ff',
    borderRadius: 6,
    fontFamily: 'Inter, system-ui, sans-serif',
  },
};

const App = () => (
  <ErrorBoundary>
    <QueryClientProvider client={queryClient}>
      <ConfigProvider locale={esES} theme={theme}>
        <AntApp>
          <TooltipProvider>
            <Toaster />
            <Sonner />
            <BrowserRouter>
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
                </Route>

                {/* 404 Not Found */}
                <Route path="*" element={<NotFound />} />
              </Routes>
            </BrowserRouter>
          </TooltipProvider>
        </AntApp>
      </ConfigProvider>
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  </ErrorBoundary>
);

export default App;
