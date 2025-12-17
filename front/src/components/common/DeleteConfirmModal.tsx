import React from 'react';
import { Modal, Form, Input } from 'antd';
import { ExclamationCircleOutlined } from '@ant-design/icons';

interface DeleteConfirmModalProps {
  title?: string;
  content: string;
  itemName: string;
  visible: boolean;
  onConfirm: (reason: string) => void;
  onCancel: () => void;
  loading?: boolean;
  requireReason?: boolean;
}

const { TextArea } = Input;

export const DeleteConfirmModal = ({
  title = '¿Confirmar eliminación?',
  content,
  itemName,
  visible,
  onConfirm,
  onCancel,
  loading = false,
  requireReason = true,
}: DeleteConfirmModalProps) => {
  const [form] = Form.useForm();

  const handleOk = () => {
    if (requireReason) {
      form.validateFields().then((values) => {
        onConfirm(values.reason);
        form.resetFields();
      });
    } else {
      onConfirm('');
      form.resetFields();
    }
  };

  const handleCancel = () => {
    form.resetFields();
    onCancel();
  };

  return (
    <Modal
      title={
        <span>
          <ExclamationCircleOutlined className="text-red-500 mr-2" />
          {title}
        </span>
      }
      open={visible}
      onOk={handleOk}
      onCancel={handleCancel}
      confirmLoading={loading}
      okText="Eliminar"
      cancelText="Cancelar"
      okButtonProps={{ danger: true }}
    >
      <div className="mb-4">
        <p className="text-gray-700 mb-2">{content}</p>
        <p className="text-gray-900 font-semibold">{itemName}</p>
      </div>

      <Form
        form={form}
        layout="vertical"
      >
        <Form.Item
          name="reason"
          label="Motivo de la eliminación"
          rules={requireReason ? [
            { required: true, message: 'Debe ingresar un motivo' },
            { min: 5, message: 'El motivo debe tener al menos 5 caracteres' },
            { max: 500, message: 'El motivo no puede exceder los 500 caracteres' }
          ] : []}
        >
          <TextArea
            rows={3}
            placeholder="Ingrese el motivo de la eliminación..."
            maxLength={500}
            showCount
          />
        </Form.Item>
      </Form>

      <div className="text-xs text-gray-500 mt-2">
        <p>Esta acción marcará el registro como eliminado pero podrá ser recuperado por un administrador.</p>
      </div>
    </Modal>
  );
};

// Hook personalizado para usar el modal de eliminación
export const useDeleteConfirm = () => {
  const [visible, setVisible] = React.useState(false);
  const [config, setConfig] = React.useState<Omit<DeleteConfirmModalProps, 'visible' | 'onCancel'>>({
    content: '',
    itemName: '',
    onConfirm: () => {},
  });

  const showDeleteConfirm = (props: Omit<DeleteConfirmModalProps, 'visible' | 'onCancel'>) => {
    setConfig(props);
    setVisible(true);
  };

  const handleCancel = () => {
    setVisible(false);
  };

  const DeleteModal = () => (
    <DeleteConfirmModal
      {...config}
      visible={visible}
      onCancel={handleCancel}
    />
  );

  return {
    showDeleteConfirm,
    DeleteModal,
  };
};
