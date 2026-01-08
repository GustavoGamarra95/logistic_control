import { Modal, Descriptions, Table, Tag, Card, Row, Col, Button, Space, Alert, QRCode, Divider } from 'antd';
import {
  FileTextOutlined,
  QrcodeOutlined,
  SendOutlined,
  SyncOutlined,
  DownloadOutlined,
  PrinterOutlined,
  FileZipOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  ClockCircleOutlined,
} from '@ant-design/icons';
import { Factura, ItemFactura, EstadoFactura } from '@/types/factura.types';
import { formatCurrency, formatDate, formatDateTime } from '@/utils/format';
import type { ColumnsType } from 'antd/es/table';
import { useRef } from 'react';
import { useReactToPrint } from 'react-to-print';
import { FacturaPrintView } from './FacturaPrintView';

interface FacturaDetailViewProps {
  open: boolean;
  onClose: () => void;
  factura: Factura | null;
  items?: ItemFactura[];
  onEnviarSifen?: () => void;
  onConsultarSifen?: () => void;
  onDescargarPDF?: () => void;
  onDescargarXML?: () => void;
  loadingSifen?: boolean;
}

const ESTADO_COLORS: Record<EstadoFactura, string> = {
  BORRADOR: 'default',
  EMITIDA: 'blue',
  ENVIADA_SIFEN: 'processing',
  APROBADA: 'success',
  RECHAZADA: 'error',
  ANULADA: 'default',
  PAGADA: 'success',
  VENCIDA: 'warning',
};

const ESTADO_SIFEN_ICONS: Record<string, React.ReactNode> = {
  APROBADO: <CheckCircleOutlined style={{ color: '#52c41a' }} />,
  RECHAZADO: <CloseCircleOutlined style={{ color: '#ff4d4f' }} />,
  PENDIENTE: <ClockCircleOutlined style={{ color: '#faad14' }} />,
  PROCESANDO: <SyncOutlined spin style={{ color: '#1890ff' }} />,
};

export const FacturaDetailView = ({
  open,
  onClose,
  factura,
  items = [],
  onEnviarSifen,
  onConsultarSifen,
  onDescargarPDF,
  onDescargarXML,
  loadingSifen = false,
}: FacturaDetailViewProps) => {
  const printRef = useRef<HTMLDivElement>(null);

  const handlePrint = useReactToPrint({
    contentRef: printRef,
    documentTitle: `Factura-${factura?.numeroFactura || 'DRAFT'}`,
  });

  if (!factura) return null;

  const itemsColumns: ColumnsType<ItemFactura> = [
    {
      title: '#',
      dataIndex: 'numeroItem',
      key: 'numeroItem',
      width: 50,
      align: 'center',
    },
    {
      title: 'Código',
      dataIndex: 'codigo',
      key: 'codigo',
      width: 100,
      render: (codigo) => codigo || '-',
    },
    {
      title: 'Descripción',
      dataIndex: 'descripcion',
      key: 'descripcion',
      ellipsis: true,
    },
    {
      title: 'Cantidad',
      dataIndex: 'cantidad',
      key: 'cantidad',
      width: 80,
      align: 'right',
    },
    {
      title: 'Unidad',
      dataIndex: 'unidadMedida',
      key: 'unidadMedida',
      width: 80,
      align: 'center',
    },
    {
      title: 'Precio Unit.',
      dataIndex: 'precioUnitario',
      key: 'precioUnitario',
      width: 120,
      align: 'right',
      render: (precio) => formatCurrency(precio, factura.moneda),
    },
    {
      title: 'IVA',
      dataIndex: 'tasaIva',
      key: 'tasaIva',
      width: 60,
      align: 'center',
      render: (tasa) => `${tasa}%`,
    },
    {
      title: 'Subtotal',
      dataIndex: 'subtotal',
      key: 'subtotal',
      width: 120,
      align: 'right',
      render: (subtotal) => formatCurrency(subtotal, factura.moneda),
    },
    {
      title: 'Total',
      dataIndex: 'total',
      key: 'total',
      width: 120,
      align: 'right',
      render: (total) => formatCurrency(total, factura.moneda),
    },
  ];

  const sifenEnviado = factura.estado === 'ENVIADA_SIFEN' || factura.estado === 'APROBADA' || factura.estado === 'RECHAZADA';

  return (
    <Modal
      title={
        <div className="flex items-center justify-between">
          <span>Factura N° {factura.numeroFactura}</span>
          <Tag color={ESTADO_COLORS[factura.estado]}>{factura.estado}</Tag>
        </div>
      }
      open={open}
      onCancel={onClose}
      width={1200}
      footer={
        <Space>
          <Button onClick={onClose}>Cerrar</Button>
          <Button icon={<PrinterOutlined />} onClick={handlePrint} type="primary">
            Imprimir
          </Button>
          {sifenEnviado && onDescargarXML && (
            <Button icon={<FileZipOutlined />} onClick={onDescargarXML}>
              XML
            </Button>
          )}
        </Space>
      }
    >
      <div className="space-y-6">
        {/* Panel SIFEN */}
        <Card
          title={
            <div className="flex items-center gap-2">
              <QrcodeOutlined />
              <span>Estado SIFEN</span>
            </div>
          }
          size="small"
          extra={
            <Space>
              {!sifenEnviado && onEnviarSifen && (
                <Button
                  type="primary"
                  icon={<SendOutlined />}
                  onClick={onEnviarSifen}
                  loading={loadingSifen}
                  size="small"
                >
                  Enviar a SIFEN
                </Button>
              )}
              {sifenEnviado && onConsultarSifen && (
                <Button
                  icon={<SyncOutlined />}
                  onClick={onConsultarSifen}
                  loading={loadingSifen}
                  size="small"
                >
                  Consultar Estado
                </Button>
              )}
            </Space>
          }
        >
          {!sifenEnviado ? (
            <Alert
              title="Factura no enviada a SIFEN"
              description="Esta factura aún no ha sido enviada al sistema SIFEN. Haga clic en 'Enviar a SIFEN' para procesarla."
              type="warning"
              showIcon
            />
          ) : (
            <Row gutter={16}>
              <Col span={16}>
                <Descriptions column={2} size="small">
                  <Descriptions.Item label="Estado SIFEN" span={2}>
                    <Space>
                      {factura.estadoSifen && ESTADO_SIFEN_ICONS[factura.estadoSifen]}
                      <Tag color={factura.estado === 'APROBADA' ? 'success' : factura.estado === 'RECHAZADA' ? 'error' : 'processing'}>
                        {factura.estadoSifen || factura.estado}
                      </Tag>
                    </Space>
                  </Descriptions.Item>

                  {factura.cdc && (
                    <Descriptions.Item label="CDC" span={2}>
                      <code className="text-xs bg-gray-100 px-2 py-1 rounded">{factura.cdc}</code>
                    </Descriptions.Item>
                  )}

                  {factura.fechaEnvioSifen && (
                    <Descriptions.Item label="Fecha Envío">
                      {formatDateTime(factura.fechaEnvioSifen)}
                    </Descriptions.Item>
                  )}

                  {factura.mensajeSifen && (
                    <Descriptions.Item label="Mensaje" span={2}>
                      <div className="text-sm text-gray-600">{factura.mensajeSifen}</div>
                    </Descriptions.Item>
                  )}

                  {factura.xmlUrl && (
                    <Descriptions.Item label="Documentos">
                      <Space>
                        <Button
                          size="small"
                          icon={<FileZipOutlined />}
                          onClick={onDescargarXML}
                        >
                          XML
                        </Button>
                        {factura.pdfUrl && (
                          <Button
                            size="small"
                            icon={<FilePdfOutlined />}
                            onClick={onDescargarPDF}
                          >
                            PDF
                          </Button>
                        )}
                      </Space>
                    </Descriptions.Item>
                  )}
                </Descriptions>
              </Col>

              {factura.qrData && (
                <Col span={8} className="flex justify-center items-center">
                  <div className="text-center">
                    <div className="mb-2 text-sm text-gray-600">QR KuDE</div>
                    <QRCode value={factura.qrData} size={150} />
                  </div>
                </Col>
              )}
            </Row>
          )}
        </Card>

        {/* Información de la Factura */}
        <Card title="Información General" size="small">
          <Descriptions column={3} size="small">
            <Descriptions.Item label="Número">
              <span className="font-mono font-semibold">{factura.numeroFactura}</span>
            </Descriptions.Item>
            <Descriptions.Item label="Timbrado">
              {factura.timbrado}
            </Descriptions.Item>
            <Descriptions.Item label="Tipo">
              <Tag>{factura.tipo}</Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Establecimiento">
              {factura.establecimiento}
            </Descriptions.Item>
            <Descriptions.Item label="Punto Expedición">
              {factura.puntoExpedicion}
            </Descriptions.Item>
            <Descriptions.Item label="Número Documento">
              {factura.numeroDocumento}
            </Descriptions.Item>

            <Descriptions.Item label="Fecha Emisión">
              {formatDate(factura.fechaEmision)}
            </Descriptions.Item>
            {factura.fechaVencimiento && (
              <Descriptions.Item label="Vencimiento">
                {formatDate(factura.fechaVencimiento)}
              </Descriptions.Item>
            )}
            <Descriptions.Item label="Moneda">
              {factura.moneda}
            </Descriptions.Item>
          </Descriptions>
        </Card>

        {/* Cliente */}
        <Card title="Cliente" size="small">
          <Descriptions column={2} size="small">
            <Descriptions.Item label="Razón Social">
              {factura.clienteRazonSocial}
            </Descriptions.Item>
            <Descriptions.Item label="RUC">
              {factura.clienteRuc}
            </Descriptions.Item>
            {factura.condicionPago && (
              <Descriptions.Item label="Condición de Pago" span={2}>
                {factura.condicionPago}
              </Descriptions.Item>
            )}
          </Descriptions>
        </Card>

        {/* Ítems de la Factura */}
        <Card title={`Ítems de la Factura (${items.length})`} size="small">
          <Table
            columns={itemsColumns}
            dataSource={items}
            rowKey="id"
            pagination={false}
            size="small"
            scroll={{ x: 900 }}
            summary={(pageData) => {
              let totalCantidad = 0;
              pageData.forEach(({ cantidad }) => {
                totalCantidad += cantidad;
              });

              return (
                <Table.Summary fixed>
                  <Table.Summary.Row className="bg-gray-50 font-semibold">
                    <Table.Summary.Cell index={0} colSpan={3} align="right">
                      TOTALES
                    </Table.Summary.Cell>
                    <Table.Summary.Cell index={1} align="right">
                      {totalCantidad}
                    </Table.Summary.Cell>
                    <Table.Summary.Cell index={2} colSpan={4} />
                  </Table.Summary.Row>
                </Table.Summary>
              );
            }}
          />
        </Card>

        {/* Totales */}
        <Card title="Resumen" size="small">
          <Row gutter={16}>
            <Col span={12}>
              {factura.observaciones && (
                <div>
                  <div className="text-sm font-medium mb-2">Observaciones:</div>
                  <div className="text-gray-700 whitespace-pre-wrap text-sm">
                    {factura.observaciones}
                  </div>
                </div>
              )}
            </Col>

            <Col span={12}>
              <div className="space-y-2 bg-gray-50 p-4 rounded">
                <div className="flex justify-between">
                  <span>Subtotal:</span>
                  <span className="font-mono">{formatCurrency(factura.subtotal, factura.moneda)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span>IVA 5%:</span>
                  <span className="font-mono">{formatCurrency(factura.iva5, factura.moneda)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span>IVA 10%:</span>
                  <span className="font-mono">{formatCurrency(factura.iva10, factura.moneda)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span>IVA Total:</span>
                  <span className="font-mono">{formatCurrency(factura.ivaTotal, factura.moneda)}</span>
                </div>
                <Divider className="my-2" />
                <div className="flex justify-between text-lg font-bold">
                  <span>TOTAL:</span>
                  <span className="font-mono text-primary">{formatCurrency(factura.total, factura.moneda)}</span>
                </div>

                {factura.tipo === 'CREDITO' && (
                  <>
                    <Divider className="my-2" />
                    <div className="flex justify-between text-sm">
                      <span>Saldo Pendiente:</span>
                      <span className="font-mono font-semibold text-orange-600">
                        {formatCurrency(factura.saldoPendiente, factura.moneda)}
                      </span>
                    </div>
                    <div className="flex justify-between text-xs text-gray-500">
                      <span>Estado de Pago:</span>
                      <Tag color={factura.estadoPago === 'PAGADO' ? 'success' : factura.estadoPago === 'VENCIDO' ? 'error' : 'warning'}>
                        {factura.estadoPago}
                      </Tag>
                    </div>
                  </>
                )}
              </div>
            </Col>
          </Row>
        </Card>
      </div>

      {/* Hidden Print Component */}
      <div style={{ display: 'none' }}>
        <FacturaPrintView ref={printRef} factura={factura} items={items} />
      </div>
    </Modal>
  );
};
