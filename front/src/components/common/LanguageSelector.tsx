import { Button, Dropdown, Space } from 'antd';
import { GlobalOutlined, CheckOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';
import type { MenuProps } from 'antd';

const LANGUAGES = [
  { code: 'es', name: 'Español', flag: '🇵🇾' },
  { code: 'en', name: 'English', flag: '🇺🇸' },
];

export const LanguageSelector = () => {
  const { i18n } = useTranslation();

  const currentLanguage = LANGUAGES.find((lang) => lang.code === i18n.language);

  const changeLanguage = (code: string) => {
    i18n.changeLanguage(code);
  };

  const menuItems: MenuProps['items'] = LANGUAGES.map((lang) => ({
    key: lang.code,
    label: (
      <Space>
        <span>{lang.flag}</span>
        <span>{lang.name}</span>
        {i18n.language === lang.code && <CheckOutlined className="ml-2" />}
      </Space>
    ),
    onClick: () => changeLanguage(lang.code),
  }));

  return (
    <Dropdown menu={{ items: menuItems }} trigger={['click']} placement="bottomRight">
      <Button
        type="text"
        icon={<GlobalOutlined />}
        className="flex items-center justify-center"
      >
        <span className="ml-1">{currentLanguage?.flag}</span>
      </Button>
    </Dropdown>
  );
};
