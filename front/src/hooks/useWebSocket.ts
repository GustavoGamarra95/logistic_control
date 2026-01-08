import { useEffect } from 'react';
import { websocketService, NotificationHandler } from '@/services/websocket.service';
import { useAuthStore } from '@/store/authStore';

/**
 * Hook para conectar/desconectar WebSocket automáticamente
 */
export function useWebSocket() {
  const isAuthenticated = useAuthStore((state) => !!state.accessToken);

  useEffect(() => {
    if (isAuthenticated) {
      websocketService.connect();

      return () => {
        websocketService.disconnect();
      };
    }
  }, [isAuthenticated]);

  return {
    isConnected: websocketService.isConnected(),
    send: websocketService.send.bind(websocketService),
  };
}

/**
 * Hook para suscribirse a notificaciones WebSocket
 */
export function useWebSocketNotifications(handler: NotificationHandler) {
  useEffect(() => {
    const unsubscribe = websocketService.subscribe(handler);

    return () => {
      unsubscribe();
    };
  }, [handler]);
}
