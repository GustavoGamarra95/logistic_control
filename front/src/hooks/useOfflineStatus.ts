import { useState, useEffect } from 'react';
import { message } from 'antd';

/**
 * Hook para monitorear el estado de conexión online/offline
 */
export function useOfflineStatus() {
  const [isOnline, setIsOnline] = useState(navigator.onLine);

  useEffect(() => {
    const handleOnline = () => {
      setIsOnline(true);
      message.success('Conexión restaurada');
    };

    const handleOffline = () => {
      setIsOnline(false);
      message.warning('Sin conexión a internet. Trabajando en modo offline.');
    };

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, []);

  return { isOnline };
}
