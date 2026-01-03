import { Modal, Descriptions, Timeline, Tag, Divider, Card, Row, Col, Button, Space } from 'antd';
import {
  ClockCircleOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  SyncOutlined,
  EnvironmentOutlined,
  DollarOutlined,
  FileTextOutlined,
} from '@ant-design/icons';
import { Pedido, EstadoPedido } from '@/types/pedido.types';
import { formatCurrency, formatDate, formatDateTime } from '@/utils/format';
import { StatusBadge } from '@/components/common/StatusBadge';

interface PedidoDetailViewProps {
  open: boolean;
  onClose: () => void;
  pedido: Pedido | null;
  historial?: {
    id: number;
    estadoAnterior: EstadoPedido | null;
    estadoNuevo: EstadoPedido;
    fechaCambio: string;
    comentario?: string;
    usuario?: string;
  }[];
}

const ESTADO_ICONS: Record<EstadoPedido, React.ReactNode> = {
  REGISTRADO: <ClockCircleOutlined style={{ color: '#1890ff' }} />,
  EN_TRANSITO: <SyncOutlined spin style={{ color: '#faad14' }} />,
  RECIBIDO: <CheckCircleOutlined style={{ color: '#52c41a' }} />,
  EN_ADUANA: <ClockCircleOutlined style={{ color: '#722ed1' }} />,
  LIBERADO: <CheckCircleOutlined style={{ color: '#13c2c2' }} />,
  EN_DEPOSITO: <EnvironmentOutlined style={{ color: '#1890ff' }} />,
  EN_REPARTO: <SyncOutlined style={{ color: '#faad14' }} />,
  ENTREGADO: <CheckCircleOutlined style={{ color: '#52c41a' }} />,
  CANCELADO: <CloseCircleOutlined style={{ color: '#ff4d4f' }} />,
  DEVUELTO: <CloseCircleOutlined style={{ color: '#f5222d' }} />,
};

const ESTADO_COLORS: Record<EstadoPedido, string> = {
  REGISTRADO: 'blue',
  EN_TRANSITO: 'orange',
  RECIBIDO: 'green',
  EN_ADUANA: 'purple',
  LIBERADO: 'cyan',
  EN_DEPOSITO: 'geekblue',
  EN_REPARTO: 'gold',
  ENTREGADO: 'success',
  CANCELADO: 'error',
  DEVUELTO: 'red',
};

export const PedidoDetailView = ({ open, onClose, pedido, historial = [] }: PedidoDetailViewProps) => {
  if (!pedido) return null;

  const handleExportPDF = () => {
    // TODO: Implementar exportación a PDF
    console.log('Exportar PDF del pedido', pedido.id);
  };

  return (
    <Modal
      title={
        <div className="flex items-center justify-between">
          <span>Detalle del Pedido: {pedido.codigoTracking}</span>
          <StatusBadge status={pedido.estado} />
        </div>
      }
      open={open}
      onCancel={onClose}
      width={1000}
      footer={
        <Space>
          <Button onClick={onClose}>Cerrar</Button>
          <Button type="primary" icon={<FileTextOutlined />} onClick={handleExportPDF}>
            Exportar PDF
          </Button>
        </Space>
      }
    >
      <div className="space-y-6">
        {/* Información General */}
        <Card title="Información General" size="small">
          <Descriptions column={2} size="small">
            <Descriptions.Item label="Código Tracking">
              <span className="font-mono font-semibold text-primary">{pedido.codigoTracking}</span>
            </Descriptions.Item>
            <Descriptions.Item label="Fecha Registro">
              {formatDateTime(pedido.fechaRegistro)}
            </Descriptions.Item>
            <Descriptions.Item label="Cliente ID">
              {pedido.clienteId}
            </Descriptions.Item>
            <Descriptions.Item label="Estado Actual">
              <Tag color={ESTADO_COLORS[pedido.estado]}>{pedido.estado}</Tag>
            </Descriptions.Item>
          </Descriptions>
        </Card>

        {/* Origen y Destino */}
        <Row gutter={16}>
          <Col span={12}>
            <Card title="Origen" size="small" className="h-full">
              <Descriptions column={1} size="small">
                <Descriptions.Item label="País">{pedido.paisOrigen}</Descriptions.Item>
                {pedido.ciudadOrigen && (
                  <Descriptions.Item label="Ciudad">{pedido.ciudadOrigen}</Descriptions.Item>
                )}
                {pedido.puertoEmbarque && (
                  <Descriptions.Item label="Puerto">{pedido.puertoEmbarque}</Descriptions.Item>
                )}
              </Descriptions>
            </Card>
          </Col>

          <Col span={12}>
            <Card title="Destino" size="small" className="h-full">
              <Descriptions column={1} size="small">
                <Descriptions.Item label="País">{pedido.paisDestino}</Descriptions.Item>
                {pedido.ciudadDestino && (
                  <Descriptions.Item label="Ciudad">{pedido.ciudadDestino}</Descriptions.Item>
                )}
                {pedido.puertoDestino && (
                  <Descriptions.Item label="Puerto">{pedido.puertoDestino}</Descriptions.Item>
                )}
                {pedido.direccionEntrega && (
                  <Descriptions.Item label="Dirección">{pedido.direccionEntrega}</Descriptions.Item>
                )}
              </Descriptions>
            </Card>
          </Col>
        </Row>

        {/* Detalles de Carga */}
        <Card title="Detalles de la Carga" size="small">
          <Descriptions column={2} size="small">
            <Descriptions.Item label="Tipo de Carga">
              <Tag>{pedido.tipoCarga}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="Descripción" span={2}>
              {pedido.descripcionMercaderia}
            </Descriptions.Item>
            <Descriptions.Item label="Peso Total">{pedido.pesoTotalKg} kg</Descriptions.Item>
            <Descriptions.Item label="Volumen Total">{pedido.volumenTotalM3} m³</Descriptions.Item>
            <Descriptions.Item label="Valor Declarado">
              {formatCurrency(pedido.valorDeclarado, pedido.moneda)}
            </Descriptions.Item>
            {pedido.numeroBlAwb && (
              <Descriptions.Item label="N° BL/AWB">{pedido.numeroBlAwb}</Descriptions.Item>
            )}
            {pedido.numeroContenedorGuia && (
              <Descriptions.Item label="N° Contenedor/Guía">{pedido.numeroContenedorGuia}</Descriptions.Item>
            )}
            {pedido.empresaTransporte && (
              <Descriptions.Item label="Transportista">{pedido.empresaTransporte}</Descriptions.Item>
            )}
          </Descriptions>
        </Card>

        {/* Fechas y Costos */}
        <Row gutter={16}>
          <Col span={12}>
            <Card title="Fechas" size="small" className="h-full">
              <Descriptions column={1} size="small">
                {pedido.fechaEstimadaLlegada && (
                  <Descriptions.Item label="Llegada Estimada">
                    {formatDate(pedido.fechaEstimadaLlegada)}
                  </Descriptions.Item>
                )}
                {pedido.fechaLlegadaReal && (
                  <Descriptions.Item label="Llegada Real">
                    {formatDate(pedido.fechaLlegadaReal)}
                  </Descriptions.Item>
                )}
              </Descriptions>
            </Card>
          </Col>

          <Col span={12}>
            <Card title="Costos" size="small" className="h-full">
              <Descriptions column={1} size="small">
                <Descriptions.Item label="Subtotal">
                  {formatCurrency(pedido.subTotal, pedido.moneda)}
                </Descriptions.Item>
                <Descriptions.Item label="IVA">
                  {formatCurrency(pedido.iva, pedido.moneda)}
                </Descriptions.Item>
                <Descriptions.Item label="Total">
                  <strong>{formatCurrency(pedido.total, pedido.moneda)}</strong>
                </Descriptions.Item>
                {pedido.requiereSeguro && pedido.valorSeguro && (
                  <Descriptions.Item label="Seguro">
                    {formatCurrency(pedido.valorSeguro, pedido.moneda)}
                  </Descriptions.Item>
                )}
                {pedido.formaPago && (
                  <Descriptions.Item label="Forma de Pago">{pedido.formaPago}</Descriptions.Item>
                )}
              </Descriptions>
            </Card>
          </Col>
        </Row>

        {/* Timeline de Estados */}
        <Card title="Historial de Estados" size="small">
          <Timeline
            mode="left"
            items={historial.length > 0 ? historial.map((item) => ({
              dot: ESTADO_ICONS[item.estadoNuevo],
              color: ESTADO_COLORS[item.estadoNuevo],
              label: formatDateTime(item.fechaCambio),
              children: (
                <div>
                  <div className="font-semibold">
                    {item.estadoAnterior && `${item.estadoAnterior} → `}
                    <Tag color={ESTADO_COLORS[item.estadoNuevo]}>{item.estadoNuevo}</Tag>
                  </div>
                  {item.comentario && (
                    <div className="text-sm text-gray-600 mt-1">{item.comentario}</div>
                  )}
                  {item.usuario && (
                    <div className="text-xs text-gray-400 mt-1">Por: {item.usuario}</div>
                  )}
                </div>
              ),
            })) : [
              {
                dot: ESTADO_ICONS[pedido.estado],
                color: ESTADO_COLORS[pedido.estado],
                label: formatDateTime(pedido.fechaRegistro),
                children: (
                  <div>
                    <div className="font-semibold">
                      <Tag color={ESTADO_COLORS[pedido.estado]}>{pedido.estado}</Tag>
                    </div>
                    <div className="text-sm text-gray-600 mt-1">Pedido registrado en el sistema</div>
                  </div>
                ),
              },
            ]}
          />
        </Card>

        {/* Observaciones */}
        {pedido.observaciones && (
          <Card title="Observaciones" size="small">
            <p className="text-gray-700 whitespace-pre-wrap">{pedido.observaciones}</p>
          </Card>
        )}
      </div>
    </Modal>
  );
};
