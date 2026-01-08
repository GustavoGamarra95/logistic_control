import { Layout as AntLayout, Avatar, Dropdown, Space, Typography, Badge, Button } from 'antd';
import { UserOutlined, LogoutOutlined, SettingOutlined, BellOutlined } from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { useAuth } from '@/hooks/useAuth';
import { useNavigate } from 'react-router-dom';

const { Header: AntHeader } = AntLayout;
const { Text } = Typography;

export const Header = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const userMenuItems: MenuProps['items'] = [
    {
      key: 'profile',
      label: 'Mi Perfil',
      icon: <UserOutlined />,
      onClick: () => navigate('/perfil'),
    },
    {
      key: 'settings',
      label: 'Configuración',
      icon: <SettingOutlined />,
      onClick: () => navigate('/configuracion'),
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
      <Space size="middle">
        <Badge count={0} showZero={false} overflowCount={99}>
          <Button
            type="text"
            icon={<BellOutlined style={{ fontSize: 18 }} />}
            className="flex items-center justify-center"
          />
        </Badge>
        <Dropdown menu={{ items: userMenuItems }} placement="bottomRight" arrow>
          <Space className="cursor-pointer hover:opacity-80 transition-opacity">
            <Avatar size="small" icon={<UserOutlined />} className="bg-primary" />
            <Text className="hidden sm:inline">
              {user?.nombre || user?.username}
            </Text>
          </Space>
        </Dropdown>
      </Space>
    </AntHeader>
  );
};
