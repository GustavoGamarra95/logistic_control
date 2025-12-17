import { useState } from 'react';
import { Table, Button, Input, Space, Card, Tag } from 'antd';
import { PlusOutlined, SearchOutlined, FileTextOutlined, QrcodeOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { PageHeader } from '@/components/common/PageHeader';

interface Factura {
  id: number;
  numero: string;
  clienteRazonSocial: string;
  fecha: string;
  montoTotal: number;
  estado: string;
  estadoSifen?: string;
}

const FacturasPage = () => {
  const [searchText, setSearchText] = useState('');

  // TODO: Replace with actual hook when API is ready
  const facturas: Factura[] = [];
  const isLoading = false;

  const getEstadoColor = (estado: string): string => {
    const colors: Record<string, string> = {
      PENDIENTE: 'orange',
      PAGADA: 'green',
      VENCIDA: 'red',
      CANCELADA: 'default',
    };
    return colors[estado] || 'default';
  };

  const columns: ColumnsType<Factura> = [
    {
      title: 'Número',
      dataIndex: 'numero',
      key: 'numero',
      width: 150,
    },
    {
      title: 'Cliente',
      dataIndex: 'clienteRazonSocial',
      key: 'clienteRazonSocial',
      ellipsis: true,
    },
    {
      title: 'Fecha',
      dataIndex: 'fecha',
      key: 'fecha',
      width: 120,
      render: (date: string) => new Date(date).toLocaleDateString('es-PY'),
    },
    {
      title: 'Monto Total',
      dataIndex: 'montoTotal',
      key: 'montoTotal',
      width: 150,
      align: 'right',
      render: (value: number) =>
        new Intl.NumberFormat('es-PY', {
          style: 'currency',
          currency: 'PYG',
          minimumFractionDigits: 0,
        }).format(value),
    },
    {
      title: 'Estado',
      dataIndex: 'estado',
      key: 'estado',
      width: 120,
      render: (estado: string) => <Tag color={getEstadoColor(estado)}>{estado}</Tag>,
    },
    {
      title: 'SIFEN',
      dataIndex: 'estadoSifen',
      key: 'estadoSifen',
      width: 120,
      render: (estado?: string) => estado ? <Tag color="blue">{estado}</Tag> : '-',
    },
    {
      title: 'Acciones',
      key: 'actions',
      width: 120,
      render: () => (
        <Space size="small">
          <Button type="text" icon={<FileTextOutlined />} size="small" title="Ver factura" />
          <Button type="text" icon={<QrcodeOutlined />} size="small" title="QR SIFEN" />
        </Space>
      ),
    },
  ];

  return (
    <div>
      <PageHeader
        title="Gestión de Facturas"
        subtitle="Facturación electrónica con SIFEN"
        extra={<Button type="primary" icon={<PlusOutlined />}>Nueva Factura</Button>}
      />
      <Card>
        <Space direction="vertical" size="middle" className="w-full">
          <Input
            placeholder="Buscar por número o cliente..."
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="max-w-md"
            allowClear
          />
          <Table
            columns={columns}
            dataSource={facturas}
            rowKey="id"
            loading={isLoading}
            pagination={{
              pageSize: 20,
              showTotal: (total) => `Total ${total} facturas`,
            }}
            scroll={{ x: 1000 }}
          />
        </Space>
      </Card>
    </div>
  );
};

export default FacturasPage;
