import { Button, Dropdown, Space } from 'antd';
import {
  BulbOutlined,
  BulbFilled,
  CheckOutlined,
} from '@ant-design/icons';
import { useThemeStore } from '@/store/themeStore';
import type { MenuProps } from 'antd';

export const ThemeToggle = () => {
  const { theme, setTheme } = useThemeStore();

  const menuItems: MenuProps['items'] = [
    {
      key: 'light',
      label: (
        <Space>
          <BulbOutlined />
          <span>Claro</span>
          {theme === 'light' && <CheckOutlined className="ml-2" />}
        </Space>
      ),
      onClick: () => setTheme('light'),
    },
    {
      key: 'dark',
      label: (
        <Space>
          <BulbFilled />
          <span>Oscuro</span>
          {theme === 'dark' && <CheckOutlined className="ml-2" />}
        </Space>
      ),
      onClick: () => setTheme('dark'),
    },
    {
      key: 'auto',
      label: (
        <Space>
          <BulbOutlined />
          <span>Automático</span>
          {theme === 'auto' && <CheckOutlined className="ml-2" />}
        </Space>
      ),
      onClick: () => setTheme('auto'),
    },
  ];

  return (
    <Dropdown menu={{ items: menuItems }} trigger={['click']} placement="bottomRight">
      <Button
        type="text"
        icon={theme === 'dark' ? <BulbFilled /> : <BulbOutlined />}
        className="flex items-center justify-center"
      />
    </Dropdown>
  );
};
