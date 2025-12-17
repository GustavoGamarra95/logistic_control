import { Tag } from 'antd';

type StatusType = 
  | 'success' 
  | 'processing' 
  | 'error' 
  | 'warning' 
  | 'default';

interface StatusBadgeProps {
  status: string;
  statusConfig?: Record<string, { label: string; color: StatusType }>;
}

const defaultStatusConfig: Record<string, { label: string; color: StatusType }> = {
  REGISTRADO: { label: 'Registrado', color: 'default' },
  EN_TRANSITO: { label: 'En Tránsito', color: 'processing' },
  RECIBIDO: { label: 'Recibido', color: 'success' },
  EN_ADUANA: { label: 'En Aduana', color: 'warning' },
  LIBERADO: { label: 'Liberado', color: 'success' },
  EN_DEPOSITO: { label: 'En Depósito', color: 'processing' },
  EN_REPARTO: { label: 'En Reparto', color: 'processing' },
  ENTREGADO: { label: 'Entregado', color: 'success' },
  CANCELADO: { label: 'Cancelado', color: 'error' },
  DEVUELTO: { label: 'Devuelto', color: 'error' },
};

export const StatusBadge = ({ status, statusConfig = defaultStatusConfig }: StatusBadgeProps) => {
  const config = statusConfig[status] || { label: status, color: 'default' as StatusType };
  
  return (
    <Tag color={config.color}>
      {config.label}
    </Tag>
  );
};
