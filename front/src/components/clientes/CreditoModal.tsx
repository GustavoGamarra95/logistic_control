import { Modal, Form, InputNumber } from 'antd';
import { useState } from 'react';

interface CreditoModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (monto: number) => void;
  currentCredito: number;
  loading?: boolean;
}

export const CreditoModal = ({
  open,
  onClose,
  onSubmit,
  currentCredito,
  loading = false,
}: CreditoModalProps) => {
  const [monto, setMonto] = useState(currentCredito);

  const handleSubmit = () => {
    onSubmit(monto);
    onClose();
  };

  return (
    <Modal
      title="Actualizar Límite de Crédito"
      open={open}
      onCancel={onClose}
      onOk={handleSubmit}
      okText="Actualizar"
      cancelText="Cancelar"
      confirmLoading={loading}
    >
      <Form layout="vertical" className="mt-4">
        <Form.Item label="Nuevo Límite de Crédito">
          <InputNumber
            value={monto}
            onChange={(value) => setMonto(value || 0)}
            className="w-full"
            min={0}
            formatter={(value) =>
              `₲ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, '.')
            }
            parser={(value) => {
              const parsed = value!.replace(/₲\s?|(\.*)/g, '');
              return Number(parsed) || 0;
            }}
          />
        </Form.Item>
        <div className="text-muted-foreground text-sm">
          Crédito actual: {new Intl.NumberFormat('es-PY', {
            style: 'currency',
            currency: 'PYG',
            minimumFractionDigits: 0,
          }).format(currentCredito)}
        </div>
      </Form>
    </Modal>
  );
};
