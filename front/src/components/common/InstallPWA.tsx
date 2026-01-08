import { useState } from 'react';
import { Button, Alert, Space } from 'antd';
import { DownloadOutlined, CloseOutlined } from '@ant-design/icons';
import { useInstallPrompt } from '@/hooks/useInstallPrompt';

export const InstallPWA = () => {
  const { isInstallable, promptInstall } = useInstallPrompt();
  const [dismissed, setDismissed] = useState(false);

  if (!isInstallable || dismissed) {
    return null;
  }

  const handleInstall = async () => {
    const installed = await promptInstall();
    if (installed) {
      setDismissed(true);
    }
  };

  const handleDismiss = () => {
    setDismissed(true);
  };

  return (
    <div className="fixed bottom-4 left-4 right-4 md:left-auto md:right-4 md:w-96 z-50 animate-fade-in">
      <Alert
        message="Instalar Aplicación"
        description="Instala Logistic Control en tu dispositivo para acceso rápido y funcionalidad offline."
        type="info"
        showIcon
        closable
        onClose={handleDismiss}
        action={
          <Space direction="vertical" className="w-full">
            <Button
              type="primary"
              size="small"
              icon={<DownloadOutlined />}
              onClick={handleInstall}
              block
            >
              Instalar
            </Button>
          </Space>
        }
        className="shadow-lg"
      />
    </div>
  );
};
