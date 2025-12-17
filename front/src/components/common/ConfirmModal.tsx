import { Modal } from 'antd';
import { ExclamationCircleOutlined } from '@ant-design/icons';

interface ConfirmModalProps {
  title: string;
  content: string;
  onConfirm: () => void;
  onCancel?: () => void;
  okText?: string;
  cancelText?: string;
  danger?: boolean;
}

export const showConfirmModal = ({
  title,
  content,
  onConfirm,
  onCancel,
  okText = 'Confirmar',
  cancelText = 'Cancelar',
  danger = false,
}: ConfirmModalProps) => {
  Modal.confirm({
    title,
    icon: <ExclamationCircleOutlined />,
    content,
    okText,
    cancelText,
    okButtonProps: { danger },
    onOk: onConfirm,
    onCancel,
  });
};
