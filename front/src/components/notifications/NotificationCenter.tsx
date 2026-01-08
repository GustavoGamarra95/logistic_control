import { useState, useEffect } from 'react';
import { Badge, Dropdown, List, Button, Empty, Tag } from 'antd';
import {
  BellOutlined,
  CheckOutlined,
  DeleteOutlined,
  InfoCircleOutlined,
  CheckCircleOutlined,
  WarningOutlined,
  CloseCircleOutlined,
} from '@ant-design/icons';
import { useNotificationStore } from '@/store/notificationStore';
import { useWebSocketNotifications } from '@/hooks/useWebSocket';
import { WebSocketNotification, NotificationType } from '@/services/websocket.service';
import { message as antMessage } from 'antd';
import { formatDateTime } from '@/utils/format';

export const NotificationCenter = () => {
  const [open, setOpen] = useState(false);
  const { notifications, unreadCount, addNotification, markAsRead, markAllAsRead, removeNotification } =
    useNotificationStore();

  // Subscribe to WebSocket notifications
  useWebSocketNotifications((notification: WebSocketNotification) => {
    addNotification(notification);

    // Show toast notification
    const messageConfig = {
      content: notification.message,
      duration: 4,
    };

    switch (notification.type) {
      case 'SUCCESS':
        antMessage.success(messageConfig);
        break;
      case 'WARNING':
        antMessage.warning(messageConfig);
        break;
      case 'ERROR':
        antMessage.error(messageConfig);
        break;
      default:
        antMessage.info(messageConfig);
    }
  });

  const getNotificationIcon = (type: NotificationType) => {
    const icons = {
      INFO: <InfoCircleOutlined className="text-blue-500" />,
      SUCCESS: <CheckCircleOutlined className="text-green-500" />,
      WARNING: <WarningOutlined className="text-orange-500" />,
      ERROR: <CloseCircleOutlined className="text-red-500" />,
    };
    return icons[type] || icons.INFO;
  };

  const handleMarkAllAsRead = () => {
    markAllAsRead();
  };

  const handleDelete = (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    removeNotification(id);
  };

  const handleNotificationClick = (id: string) => {
    markAsRead(id);
  };

  const dropdownContent = (
    <div className="w-96 bg-white rounded-lg shadow-lg">
      <div className="p-4 border-b flex justify-between items-center">
        <div className="font-semibold text-lg">Notificaciones</div>
        {unreadCount > 0 && (
          <Button type="link" size="small" onClick={handleMarkAllAsRead}>
            Marcar todas como leídas
          </Button>
        )}
      </div>

      <div className="max-h-96 overflow-y-auto">
        {notifications.length === 0 ? (
          <Empty
            image={Empty.PRESENTED_IMAGE_SIMPLE}
            description="No hay notificaciones"
            className="py-8"
          />
        ) : (
          <List
            dataSource={notifications}
            renderItem={(notification: any) => (
              <List.Item
                key={notification.id}
                className={`cursor-pointer hover:bg-gray-50 px-4 ${
                  !notification.read ? 'bg-blue-50' : ''
                }`}
                onClick={() => handleNotificationClick(notification.id)}
                actions={[
                  <Button
                    type="text"
                    size="small"
                    icon={<DeleteOutlined />}
                    onClick={(e) => handleDelete(notification.id, e)}
                    danger
                  />,
                ]}
              >
                <List.Item.Meta
                  avatar={getNotificationIcon(notification.type)}
                  title={
                    <div className="flex items-center gap-2">
                      <span className={!notification.read ? 'font-semibold' : ''}>
                        {notification.title}
                      </span>
                      {!notification.read && (
                        <div className="w-2 h-2 bg-blue-500 rounded-full" />
                      )}
                    </div>
                  }
                  description={
                    <div>
                      <div className="text-sm mb-1">{notification.message}</div>
                      <div className="text-xs text-gray-400">
                        {formatDateTime(notification.timestamp)}
                      </div>
                    </div>
                  }
                />
              </List.Item>
            )}
          />
        )}
      </div>
    </div>
  );

  return (
    <Dropdown
      dropdownRender={() => dropdownContent}
      trigger={['click']}
      open={open}
      onOpenChange={setOpen}
      placement="bottomRight"
    >
      <Badge count={unreadCount} offset={[-5, 5]}>
        <Button
          type="text"
          icon={<BellOutlined className="text-lg" />}
          className="flex items-center justify-center"
        />
      </Badge>
    </Dropdown>
  );
};
