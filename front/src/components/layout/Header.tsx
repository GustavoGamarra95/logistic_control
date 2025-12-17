import { Layout as AntLayout, Avatar, Dropdown, Space, Typography } from 'antd';
import { UserOutlined, LogoutOutlined, SettingOutlined } from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { useAuth } from '@/hooks/useAuth';

const { Header: AntHeader } = AntLayout;
const { Text } = Typography;

export const Header = () => {
  const { user, logout } = useAuth();

  const userMenuItems: MenuProps['items'] = [
    {
      key: 'profile',
      label: 'Mi Perfil',
      icon: <UserOutlined />,
    },
    {
      key: 'settings',
      label: 'Configuración',
      icon: <SettingOutlined />,
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      label: 'Cerrar Sesión',
      icon: <LogoutOutlined />,
      onClick: logout,
      danger: true,
    },
  ];

  return (
    <AntHeader className="bg-card border-b border-border flex items-center justify-end px-6 shadow-sm">
      <Dropdown menu={{ items: userMenuItems }} placement="bottomRight" arrow>
        <Space className="cursor-pointer hover:opacity-80 transition-opacity">
          <Avatar size="small" icon={<UserOutlined />} className="bg-primary" />
          <Text className="hidden sm:inline">
            {user?.nombre || user?.username}
          </Text>
        </Space>
      </Dropdown>
    </AntHeader>
  );
};
