import { Layout, Menu } from 'antd';
import {
  DashboardOutlined,
  UserOutlined,
  ShoppingOutlined,
  InboxOutlined,
  AppstoreOutlined,
  FileTextOutlined,
  TeamOutlined,
  SettingOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons';
import { useLocation, useNavigate } from 'react-router-dom';
import { useUIStore } from '@/store/uiStore';
import { usePermissions } from '@/hooks/usePermissions';
import type { MenuProps } from 'antd';

const { Sider } = Layout;

export const Sidebar = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { sidebarCollapsed, toggleSidebar } = useUIStore();
  const { canAccess } = usePermissions();

  const menuItems: MenuProps['items'] = [
    {
      key: '/dashboard',
      icon: <DashboardOutlined />,
      label: 'Dashboard',
      onClick: () => navigate('/dashboard'),
    },
    canAccess('clientes') && {
      key: '/clientes',
      icon: <UserOutlined />,
      label: 'Clientes',
      onClick: () => navigate('/clientes'),
    },
    canAccess('pedidos') && {
      key: '/pedidos',
      icon: <ShoppingOutlined />,
      label: 'Pedidos',
      onClick: () => navigate('/pedidos'),
    },
    canAccess('contenedores') && {
      key: '/contenedores',
      icon: <InboxOutlined />,
      label: 'Contenedores',
      onClick: () => navigate('/contenedores'),
    },
    canAccess('productos') && {
      key: '/productos',
      icon: <AppstoreOutlined />,
      label: 'Productos',
      onClick: () => navigate('/productos'),
    },
    canAccess('inventario') && {
      key: '/inventario',
      icon: <InboxOutlined />,
      label: 'Inventario',
      onClick: () => navigate('/inventario'),
    },
    canAccess('facturas') && {
      key: '/facturas',
      icon: <FileTextOutlined />,
      label: 'Facturas',
      onClick: () => navigate('/facturas'),
    },
    canAccess('proveedores') && {
      key: '/proveedores',
      icon: <TeamOutlined />,
      label: 'Proveedores',
      onClick: () => navigate('/proveedores'),
    },
    canAccess('usuarios') && {
      key: '/usuarios',
      icon: <SettingOutlined />,
      label: 'Usuarios',
      onClick: () => navigate('/usuarios'),
    },
  ].filter(Boolean);

  return (
    <Sider
      collapsible
      collapsed={sidebarCollapsed}
      onCollapse={toggleSidebar}
      className="!bg-sidebar"
      trigger={
        <div className="flex items-center justify-center py-4 hover:bg-sidebar-accent transition-colors">
          {sidebarCollapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
        </div>
      }
    >
      <div className="h-16 flex items-center justify-center border-b border-sidebar-border">
        {!sidebarCollapsed && (
          <h1 className="text-sidebar-foreground text-xl font-bold">LogiControl</h1>
        )}
        {sidebarCollapsed && (
          <h1 className="text-sidebar-foreground text-2xl font-bold">LC</h1>
        )}
      </div>
      
      <Menu
        theme="dark"
        mode="inline"
        selectedKeys={[location.pathname]}
        items={menuItems}
        className="!bg-sidebar !border-r-0"
      />
    </Sider>
  );
};
