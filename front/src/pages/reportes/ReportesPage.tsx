import { useState } from 'react';
import { Card, Row, Col, Button, Select, DatePicker, Form, Space, Divider, List, Tag } from 'antd';
import {
  FilePdfOutlined,
  FileExcelOutlined,
  DownloadOutlined,
  EyeOutlined,
  BarChartOutlined,
  DollarOutlined,
  InboxOutlined,
  ShoppingOutlined,
} from '@ant-design/icons';
import { PageHeader } from '@/components/common/PageHeader';
import dayjs, { Dayjs } from 'dayjs';

const { RangePicker } = DatePicker;

interface ReportConfig {
  id: string;
  name: string;
  description: string;
  category: 'PEDIDOS' | 'FACTURAS' | 'INVENTARIO' | 'FINANCIERO';
  icon: React.ReactNode;
  parameters: {
    requiresDateRange?: boolean;
    requiresCliente?: boolean;
    requiresProveedor?: boolean;
    requiresEstado?: boolean;
  };
}

const AVAILABLE_REPORTS: ReportConfig[] = [
  {
    id: 'pedidos-por-estado',
    name: 'Pedidos por Estado',
    description: 'Resumen de pedidos agrupados por estado en un rango de fechas',
    category: 'PEDIDOS',
    icon: <ShoppingOutlined />,
    parameters: {
      requiresDateRange: true,
      requiresEstado: false,
    },
  },
  {
    id: 'pedidos-detallado',
    name: 'Pedidos Detallado',
    description: 'Listado detallado de todos los pedidos con sus items y costos',
    category: 'PEDIDOS',
    icon: <BarChartOutlined />,
    parameters: {
      requiresDateRange: true,
      requiresCliente: true,
    },
  },
  {
    id: 'facturas-emitidas',
    name: 'Facturas Emitidas',
    description: 'Reporte de facturas emitidas por rango de fechas',
    category: 'FACTURAS',
    icon: <FilePdfOutlined />,
    parameters: {
      requiresDateRange: true,
      requiresCliente: false,
    },
  },
  {
    id: 'facturas-sifen',
    name: 'Estado SIFEN',
    description: 'Reporte de facturas enviadas a SIFEN con su estado de aprobación',
    category: 'FACTURAS',
    icon: <FileExcelOutlined />,
    parameters: {
      requiresDateRange: true,
    },
  },
  {
    id: 'inventario-valorizado',
    name: 'Inventario Valorizado',
    description: 'Reporte de inventario actual con valorización',
    category: 'INVENTARIO',
    icon: <InboxOutlined />,
    parameters: {},
  },
  {
    id: 'movimientos-inventario',
    name: 'Movimientos de Inventario',
    description: 'Detalle de entradas y salidas de inventario',
    category: 'INVENTARIO',
    icon: <BarChartOutlined />,
    parameters: {
      requiresDateRange: true,
    },
  },
  {
    id: 'ingresos-por-periodo',
    name: 'Ingresos por Período',
    description: 'Análisis de ingresos por facturas en un período',
    category: 'FINANCIERO',
    icon: <DollarOutlined />,
    parameters: {
      requiresDateRange: true,
    },
  },
  {
    id: 'cuentas-por-cobrar',
    name: 'Cuentas por Cobrar',
    description: 'Reporte de facturas pendientes de pago',
    category: 'FINANCIERO',
    icon: <DollarOutlined />,
    parameters: {
      requiresCliente: false,
    },
  },
];

const ReportesPage = () => {
  const [selectedReport, setSelectedReport] = useState<ReportConfig | null>(null);
  const [dateRange, setDateRange] = useState<[Dayjs, Dayjs] | null>(null);
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();

  const handleGenerateReport = async (format: 'PDF' | 'EXCEL') => {
    if (!selectedReport) return;

    setLoading(true);

    try {
      const params: any = {
        reportId: selectedReport.id,
        format: format,
      };

      if (selectedReport.parameters.requiresDateRange && dateRange) {
        params.fechaDesde = dateRange[0].format('YYYY-MM-DD');
        params.fechaHasta = dateRange[1].format('YYYY-MM-DD');
      }

      // Get form values
      const values = form.getFieldsValue();
      Object.assign(params, values);

      // TODO: Call API to generate report
      // const blob = await reportesApi.generate(params);
      // const url = window.URL.createObjectURL(blob);
      // const link = document.createElement('a');
      // link.href = url;
      // link.download = `${selectedReport.id}-${dayjs().format('YYYY-MM-DD')}.${format.toLowerCase()}`;
      // link.click();

      console.log('Generating report with params:', params);

      // Simulate download
      setTimeout(() => {
        alert(`Reporte "${selectedReport.name}" generado en formato ${format}`);
        setLoading(false);
      }, 2000);
    } catch (error) {
      console.error('Error generating report:', error);
      setLoading(false);
    }
  };

  const handlePreviewReport = async () => {
    if (!selectedReport) return;

    setLoading(true);

    try {
      // TODO: Call API to preview report
      console.log('Previewing report:', selectedReport.id);

      setTimeout(() => {
        alert(`Vista previa del reporte "${selectedReport.name}"`);
        setLoading(false);
      }, 1500);
    } catch (error) {
      console.error('Error previewing report:', error);
      setLoading(false);
    }
  };

  const getCategoryColor = (category: string) => {
    const colors: Record<string, string> = {
      PEDIDOS: 'blue',
      FACTURAS: 'green',
      INVENTARIO: 'orange',
      FINANCIERO: 'purple',
    };
    return colors[category] || 'default';
  };

  return (
    <div>
      <PageHeader
        title="Reportes"
        subtitle="Generación de reportes e informes del sistema"
      />

      <Row gutter={[16, 16]}>
        {/* Reports List */}
        <Col xs={24} lg={10}>
          <Card title="Reportes Disponibles" size="small">
            <List
              dataSource={AVAILABLE_REPORTS}
              renderItem={(report) => (
                <List.Item
                  className={`cursor-pointer hover:bg-gray-50 ${
                    selectedReport?.id === report.id ? 'bg-blue-50' : ''
                  }`}
                  onClick={() => {
                    setSelectedReport(report);
                    form.resetFields();
                  }}
                  extra={
                    <Tag color={getCategoryColor(report.category)}>
                      {report.category}
                    </Tag>
                  }
                >
                  <List.Item.Meta
                    avatar={
                      <div className="text-2xl text-blue-500">{report.icon}</div>
                    }
                    title={report.name}
                    description={report.description}
                  />
                </List.Item>
              )}
            />
          </Card>
        </Col>

        {/* Report Parameters */}
        <Col xs={24} lg={14}>
          <Card
            title={selectedReport ? selectedReport.name : 'Seleccione un Reporte'}
            size="small"
          >
            {!selectedReport ? (
              <div className="text-center py-12 text-gray-400">
                <BarChartOutlined className="text-6xl mb-4" />
                <p>Seleccione un reporte de la lista para configurar y generar</p>
              </div>
            ) : (
              <Form form={form} layout="vertical">
                <Space vertical size="middle" className="w-full">
                  {/* Date Range */}
                  {selectedReport.parameters.requiresDateRange && (
                    <Form.Item
                      label="Rango de Fechas"
                      name="dateRange"
                      rules={[{ required: true, message: 'Seleccione un rango de fechas' }]}
                    >
                      <RangePicker
                        className="w-full"
                        format="DD/MM/YYYY"
                        onChange={(dates) => setDateRange(dates as [Dayjs, Dayjs])}
                        defaultValue={[dayjs().subtract(30, 'days'), dayjs()]}
                      />
                    </Form.Item>
                  )}

                  {/* Cliente Select */}
                  {selectedReport.parameters.requiresCliente && (
                    <Form.Item label="Cliente (Opcional)" name="clienteId">
                      <Select
                        placeholder="Seleccione un cliente"
                        allowClear
                        showSearch
                        filterOption={(input, option) =>
                          (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
                        }
                        options={[
                          { value: 1, label: 'Cliente 1' },
                          { value: 2, label: 'Cliente 2' },
                          // TODO: Load from API
                        ]}
                      />
                    </Form.Item>
                  )}

                  {/* Proveedor Select */}
                  {selectedReport.parameters.requiresProveedor && (
                    <Form.Item label="Proveedor (Opcional)" name="proveedorId">
                      <Select
                        placeholder="Seleccione un proveedor"
                        allowClear
                        showSearch
                        options={[
                          { value: 1, label: 'Proveedor 1' },
                          { value: 2, label: 'Proveedor 2' },
                          // TODO: Load from API
                        ]}
                      />
                    </Form.Item>
                  )}

                  {/* Estado Select */}
                  {selectedReport.parameters.requiresEstado && (
                    <Form.Item label="Estado (Opcional)" name="estado">
                      <Select
                        placeholder="Seleccione un estado"
                        allowClear
                        options={[
                          { value: 'REGISTRADO', label: 'Registrado' },
                          { value: 'EN_TRANSITO', label: 'En Tránsito' },
                          { value: 'ENTREGADO', label: 'Entregado' },
                          // TODO: Dynamic based on report type
                        ]}
                      />
                    </Form.Item>
                  )}

                  <Divider />

                  {/* Action Buttons */}
                  <Space wrap>
                    <Button
                      type="default"
                      icon={<EyeOutlined />}
                      onClick={handlePreviewReport}
                      loading={loading}
                    >
                      Vista Previa
                    </Button>

                    <Button
                      type="primary"
                      icon={<FilePdfOutlined />}
                      onClick={() => handleGenerateReport('PDF')}
                      loading={loading}
                    >
                      Generar PDF
                    </Button>

                    <Button
                      icon={<FileExcelOutlined />}
                      onClick={() => handleGenerateReport('EXCEL')}
                      loading={loading}
                    >
                      Generar Excel
                    </Button>
                  </Space>

                  {/* Report Info */}
                  <Card size="small" className="bg-blue-50 border-blue-200">
                    <div className="text-sm text-gray-700">
                      <strong>Descripción:</strong> {selectedReport.description}
                    </div>
                    <div className="text-xs text-gray-500 mt-2">
                      Categoría: <Tag color={getCategoryColor(selectedReport.category)}>{selectedReport.category}</Tag>
                    </div>
                  </Card>
                </Space>
              </Form>
            )}
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default ReportesPage;
