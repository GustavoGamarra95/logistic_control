import { useState } from 'react';
import { Table, Button, Input, Space, Card, Tag } from 'antd';
import { SearchOutlined, InboxOutlined, SwapOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { PageHeader } from '@/components/common/PageHeader';

interface Inventario {
  id: number;
  productoId: number;
  productoDescripcion: string;
  cantidadDisponible: number;
  cantidadReservada: number;
  ubicacion: string;
  lote?: string;
  fechaIngreso: string;
}

const InventarioPage = () => {
  const [searchText, setSearchText] = useState('');

  // TODO: Replace with actual hook when API is ready
  const inventario: Inventario[] = [];
  const isLoading = false;

  const columns: ColumnsType<Inventario> = [
    {
      title: 'Producto',
      dataIndex: 'productoDescripcion',
      key: 'productoDescripcion',
      width: 250,
      ellipsis: true,
    },
    {
      title: 'Ubicación',
      dataIndex: 'ubicacion',
      key: 'ubicacion',
      width: 150,
    },
    {
      title: 'Lote',
      dataIndex: 'lote',
      key: 'lote',
      width: 120,
    },
    {
      title: 'Disponible',
      dataIndex: 'cantidadDisponible',
      key: 'cantidadDisponible',
      width: 120,
      align: 'right',
      render: (value: number) => <Tag color="green">{value}</Tag>,
    },
    {
      title: 'Reservada',
      dataIndex: 'cantidadReservada',
      key: 'cantidadReservada',
      width: 120,
      align: 'right',
      render: (value: number) => value > 0 ? <Tag color="orange">{value}</Tag> : '-',
    },
    {
      title: 'Fecha Ingreso',
      dataIndex: 'fechaIngreso',
      key: 'fechaIngreso',
      width: 130,
      render: (date: string) => new Date(date).toLocaleDateString('es-PY'),
    },
    {
      title: 'Acciones',
      key: 'actions',
      width: 120,
      render: () => (
        <Space size="small">
          <Button type="text" icon={<InboxOutlined />} size="small" title="Entrada" />
          <Button type="text" icon={<SwapOutlined />} size="small" title="Salida" />
        </Space>
      ),
    },
  ];

  return (
    <div>
      <PageHeader
        title="Gestión de Inventario"
        subtitle="Control de stock y almacenamiento"
      />
      <Card>
        <Space direction="vertical" size="middle" className="w-full">
          <Input
            placeholder="Buscar por producto o ubicación..."
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="max-w-md"
            allowClear
          />
          <Table
            columns={columns}
            dataSource={inventario}
            rowKey="id"
            loading={isLoading}
            pagination={{
              pageSize: 20,
              showTotal: (total) => `Total ${total} registros`,
            }}
            scroll={{ x: 1000 }}
          />
        </Space>
      </Card>
    </div>
  );
};

export default InventarioPage;
