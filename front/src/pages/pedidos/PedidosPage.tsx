import { useState } from 'react';
import { Table, Button, Input, Space, Card, Tag, Select } from 'antd';
import { PlusOutlined, SearchOutlined, EyeOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { usePedidos } from '@/hooks/usePedidos';
import { usePagination } from '@/hooks/usePagination';
import { useDebounce } from '@/hooks/useDebounce';
import { Pedido, EstadoPedido } from '@/types/pedido.types';
import { PedidoFormData } from '@/schemas/pedido.schema';
import { PageHeader } from '@/components/common/PageHeader';
import { StatusBadge } from '@/components/common/StatusBadge';
import { showConfirmModal } from '@/components/common/ConfirmModal';
import { formatCurrency, formatDate } from '@/utils/format';
import { PedidoFormModal } from '@/components/pedidos/PedidoFormModal';
import { PedidoDetailView } from '@/components/pedidos/PedidoDetailView';

const PedidosPage = () => {
  const [searchText, setSearchText] = useState('');
  const [estadoFilter, setEstadoFilter] = useState<EstadoPedido | 'TODOS'>('TODOS');
  const [modalOpen, setModalOpen] = useState(false);
  const [detailModalOpen, setDetailModalOpen] = useState(false);
  const [selectedPedido, setSelectedPedido] = useState<Pedido | undefined>(undefined);

  const debouncedSearch = useDebounce(searchText, 300);
  const { page, size, handlePageChange, handleSizeChange } = usePagination();

  const {
    pedidos,
    pagination,
    isLoading,
    createPedido,
    updatePedido,
    deletePedido,
    isCreating,
    isUpdating,
  } = usePedidos({
    page,
    size,
    search: debouncedSearch,
    estado: estadoFilter !== 'TODOS' ? estadoFilter : undefined,
  });

  const handleOpenModal = (pedido?: Pedido) => {
    setSelectedPedido(pedido);
    setModalOpen(true);
  };

  const handleCloseModal = () => {
    setSelectedPedido(undefined);
    setModalOpen(false);
  };

  const handleViewDetail = (pedido: Pedido) => {
    setSelectedPedido(pedido);
    setDetailModalOpen(true);
  };

  const handleCloseDetailModal = () => {
    setSelectedPedido(undefined);
    setDetailModalOpen(false);
  };

  const handleSubmit = (data: PedidoFormData) => {
    if (selectedPedido) {
      updatePedido({ id: selectedPedido.id, data });
    } else {
      createPedido(data);
    }
  };

  const handleDelete = (pedido: Pedido) => {
    showConfirmModal({
      title: '¿Eliminar Pedido?',
      content: `¿Está seguro de eliminar el pedido ${pedido.codigoTracking}? Esta acción no se puede deshacer.`,
      onConfirm: () => deletePedido(pedido.id),
      danger: true,
      okText: 'Eliminar',
    });
  };

  const columns: ColumnsType<Pedido> = [
    {
      title: 'Código Tracking',
      dataIndex: 'codigoTracking',
      key: 'codigoTracking',
      width: 150,
      fixed: 'left',
      render: (codigo: string) => (
        <span className="font-mono font-semibold text-primary">{codigo}</span>
      ),
    },
    {
      title: 'Fecha Registro',
      dataIndex: 'fechaRegistro',
      key: 'fechaRegistro',
      width: 120,
      render: (fecha: string) => formatDate(fecha),
    },
    {
      title: 'Origen → Destino',
      key: 'ruta',
      width: 200,
      render: (_, record) => (
        <div className="text-sm">
          <div>{record.paisOrigen}</div>
          <div className="text-muted-foreground">↓</div>
          <div>{record.paisDestino}</div>
        </div>
      ),
    },
    {
      title: 'Tipo Carga',
      dataIndex: 'tipoCarga',
      key: 'tipoCarga',
      width: 120,
      render: (tipo: string) => {
        const colors: Record<string, string> = {
          FCL: 'blue',
          LCL: 'cyan',
          GRANEL: 'green',
          PERECEDERO: 'orange',
          PELIGROSO: 'red',
          FRAGIL: 'yellow',
        };
        return <Tag color={colors[tipo] || 'default'}>{tipo}</Tag>;
      },
    },
    {
      title: 'Estado',
      dataIndex: 'estado',
      key: 'estado',
      width: 150,
      render: (estado: EstadoPedido) => <StatusBadge status={estado} />,
    },
    {
      title: 'Peso (kg)',
      dataIndex: 'pesoTotalKg',
      key: 'pesoTotalKg',
      width: 100,
      align: 'right',
      render: (peso: number) => (peso || 0).toLocaleString('es-PY'),
    },
    {
      title: 'Total',
      dataIndex: 'total',
      key: 'total',
      width: 120,
      align: 'right',
      render: (total: number, record) => formatCurrency(total, record.moneda),
    },
    {
      title: 'Acciones',
      key: 'actions',
      width: 120,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="text"
            icon={<EyeOutlined />}
            size="small"
            onClick={() => handleViewDetail(record)}
            title="Ver detalle"
          />
          <Button
            type="text"
            icon={<EditOutlined />}
            size="small"
            onClick={() => handleOpenModal(record)}
            title="Editar"
          />
          <Button
            type="text"
            danger
            icon={<DeleteOutlined />}
            size="small"
            onClick={() => handleDelete(record)}
            title="Eliminar"
          />
        </Space>
      ),
    },
  ];

  return (
    <div>
      <PageHeader
        title="Gestión de Pedidos"
        subtitle="Administra y da seguimiento a todos los pedidos"
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={() => handleOpenModal()}>
            Nuevo Pedido
          </Button>
        }
      />

      <Card>
        <Space direction="vertical" size="middle" className="w-full">
          <Space wrap>
            <Input
              placeholder="Buscar por código de tracking, cliente..."
              prefix={<SearchOutlined />}
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              className="w-80"
              allowClear
            />
            
            <Select
              value={estadoFilter}
              onChange={setEstadoFilter}
              className="w-48"
              options={[
                { value: 'TODOS', label: 'Todos los estados' },
                { value: 'REGISTRADO', label: 'Registrado' },
                { value: 'EN_TRANSITO', label: 'En Tránsito' },
                { value: 'RECIBIDO', label: 'Recibido' },
                { value: 'EN_ADUANA', label: 'En Aduana' },
                { value: 'LIBERADO', label: 'Liberado' },
                { value: 'EN_DEPOSITO', label: 'En Depósito' },
                { value: 'EN_REPARTO', label: 'En Reparto' },
                { value: 'ENTREGADO', label: 'Entregado' },
                { value: 'CANCELADO', label: 'Cancelado' },
                { value: 'DEVUELTO', label: 'Devuelto' },
              ]}
            />
          </Space>

          <Table
            columns={columns}
            dataSource={pedidos}
            rowKey="id"
            loading={isLoading}
            pagination={{
              current: pagination.current,
              pageSize: pagination.pageSize,
              total: pagination.total,
              onChange: handlePageChange,
              onShowSizeChange: (_, size) => handleSizeChange(size),
              showSizeChanger: true,
              showTotal: (total) => `Total ${total} pedidos`,
              pageSizeOptions: ['10', '20', '50', '100'],
            }}
            scroll={{ x: 1400 }}
          />
        </Space>
      </Card>

      <PedidoFormModal
        open={modalOpen}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
        initialData={selectedPedido}
        loading={isCreating || isUpdating}
      />

      <PedidoDetailView
        open={detailModalOpen}
        onClose={handleCloseDetailModal}
        pedido={selectedPedido || null}
      />
    </div>
  );
};

export default PedidosPage;
