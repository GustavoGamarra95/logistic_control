import { useState } from 'react';
import { Table, Button, Input, Space, Card, Tag, Select } from 'antd';
import { PlusOutlined, SearchOutlined, InboxOutlined, SwapOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { PageHeader } from '@/components/common/PageHeader';
import { useInventario } from '@/hooks/useInventario';
import { usePagination } from '@/hooks/usePagination';
import { useDebounce } from '@/hooks/useDebounce';
import { Inventario, EstadoInventario, CreateInventarioRequest } from '@/types/inventario.types';
import { InventarioEntryModal } from '@/components/inventario/InventarioEntryModal';
import { InventarioExitModal } from '@/components/inventario/InventarioExitModal';
import { showConfirmModal } from '@/components/common/ConfirmModal';
import { formatDate, formatDateTime } from '@/utils/format';

const InventarioPage = () => {
  const [searchText, setSearchText] = useState('');
  const [estadoFilter, setEstadoFilter] = useState<EstadoInventario | 'TODOS'>('TODOS');
  const [entryModalOpen, setEntryModalOpen] = useState(false);
  const [exitModalOpen, setExitModalOpen] = useState(false);

  const debouncedSearch = useDebounce(searchText, 300);
  const { page, size, handlePageChange, handleSizeChange } = usePagination();

  const {
    inventario,
    pagination,
    isLoading,
    createInventario,
    registrarSalida,
    reservar,
    deleteInventario,
    isCreating,
  } = useInventario({
    page,
    size,
    search: debouncedSearch,
    estado: estadoFilter !== 'TODOS' ? estadoFilter : undefined,
  });

  const handleOpenEntryModal = () => {
    setEntryModalOpen(true);
  };

  const handleCloseEntryModal = () => {
    setEntryModalOpen(false);
  };

  const handleOpenExitModal = () => {
    setExitModalOpen(true);
  };

  const handleCloseExitModal = () => {
    setExitModalOpen(false);
  };

  const handleEntrySubmit = async (data: CreateInventarioRequest) => {
    try {
      await createInventario(data);
      handleCloseEntryModal();
    } catch (error) {
      // El error ya se maneja en el hook con message.error()
      // No cerramos el modal para que el usuario pueda corregir
    }
  };

  const handleExitSubmit = (data: any) => {
    // Handle different movement types
    switch (data.tipoMovimiento) {
      case 'SALIDA':
        registrarSalida({ id: data.inventarioId, cantidad: data.cantidad });
        break;
      case 'RESERVA':
        reservar({ id: data.inventarioId, cantidad: data.cantidad });
        break;
      case 'TRANSFERENCIA':
      case 'AJUSTE':
        // TODO: Implement when backend supports these operations
        console.warn(`Operation ${data.tipoMovimiento} not yet implemented`);
        break;
      default:
        console.error('Unknown movement type:', data.tipoMovimiento);
    }
    handleCloseExitModal();
  };

  const handleDelete = (record: Inventario) => {
    showConfirmModal({
      title: '¿Eliminar registro de inventario?',
      content: `¿Está seguro de eliminar el inventario de ${record.productoDescripcion} en ${record.ubicacion}? Esta acción no se puede deshacer.`,
      onConfirm: () => deleteInventario(record.id),
      danger: true,
      okText: 'Eliminar',
    });
  };

  const getEstadoColor = (estado: EstadoInventario): string => {
    const colors: Record<EstadoInventario, string> = {
      DISPONIBLE: 'success',
      RESERVADO: 'warning',
      EN_CUARENTENA: 'processing',
      DAÑADO: 'error',
      VENCIDO: 'default',
    };
    return colors[estado] || 'default';
  };

  const columns: ColumnsType<Inventario> = [
    {
      title: 'Producto',
      dataIndex: 'productoDescripcion',
      key: 'productoDescripcion',
      width: 250,
      ellipsis: true,
      fixed: 'left',
    },
    {
      title: 'Ubicación',
      dataIndex: 'ubicacion',
      key: 'ubicacion',
      width: 180,
      render: (ubicacion: string) => (
        <span className="font-mono text-xs">{ubicacion}</span>
      ),
    },
    {
      title: 'Lote',
      dataIndex: 'lote',
      key: 'lote',
      width: 120,
      render: (lote?: string) => lote || '-',
    },
    {
      title: 'Estado',
      dataIndex: 'estado',
      key: 'estado',
      width: 120,
      render: (estado: EstadoInventario) => (
        <Tag color={getEstadoColor(estado)}>{estado}</Tag>
      ),
    },
    {
      title: 'Disponible',
      dataIndex: 'cantidadDisponible',
      key: 'cantidadDisponible',
      width: 100,
      align: 'right',
      render: (value: number) => (
        <Tag color={value > 0 ? 'green' : 'red'}>
          {value}
        </Tag>
      ),
    },
    {
      title: 'Reservada',
      dataIndex: 'cantidadReservada',
      key: 'cantidadReservada',
      width: 100,
      align: 'right',
      render: (value: number) => value > 0 ? <Tag color="orange">{value}</Tag> : '-',
    },
    {
      title: 'Fecha Ingreso',
      dataIndex: 'fechaEntrada',
      key: 'fechaEntrada',
      width: 180,
      render: (date: string, record: Inventario) => {
        const fecha = date || record.fechaIngreso;
        return fecha ? formatDateTime(fecha) : '--';
      },
    },
    {
      title: 'Vencimiento',
      dataIndex: 'fechaVencimiento',
      key: 'fechaVencimiento',
      width: 180,
      render: (date?: string) => {
        if (!date) return '-';
        const vencimiento = new Date(date);
        const hoy = new Date();
        const diasRestantes = Math.ceil((vencimiento.getTime() - hoy.getTime()) / (1000 * 60 * 60 * 24));

        return (
          <Tag color={diasRestantes < 30 ? 'error' : diasRestantes < 90 ? 'warning' : 'default'}>
            {formatDateTime(date)}
          </Tag>
        );
      },
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
            icon={<SwapOutlined />}
            size="small"
            onClick={handleOpenExitModal}
            title="Registrar salida"
            disabled={record.cantidadDisponible === 0}
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
        title="Gestión de Inventario"
        subtitle="Control de stock y almacenamiento"
        extra={
          <Space>
            <Button
              icon={<InboxOutlined />}
              onClick={handleOpenEntryModal}
            >
              Nueva Entrada
            </Button>
            <Button
              type="primary"
              icon={<SwapOutlined />}
              onClick={handleOpenExitModal}
            >
              Registrar Salida
            </Button>
          </Space>
        }
      />

      <Card>
        <Space vertical size="middle" className="w-full">
          <Space wrap>
            <Input
              placeholder="Buscar por producto o ubicación..."
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
                { value: 'DISPONIBLE', label: 'Disponible' },
                { value: 'RESERVADO', label: 'Reservado' },
                { value: 'EN_CUARENTENA', label: 'En Cuarentena' },
                { value: 'DAÑADO', label: 'Dañado' },
                { value: 'VENCIDO', label: 'Vencido' },
              ]}
            />
          </Space>

          <Table
            columns={columns}
            dataSource={inventario}
            rowKey="id"
            loading={isLoading}
            pagination={{
              current: pagination.current,
              pageSize: pagination.pageSize,
              total: pagination.total,
              onChange: handlePageChange,
              onShowSizeChange: (_, size) => handleSizeChange(size),
              showSizeChanger: true,
              showTotal: (total) => `Total ${total} registros`,
              pageSizeOptions: ['10', '20', '50', '100'],
            }}
            scroll={{ x: 1400 }}
          />
        </Space>
      </Card>

      <InventarioEntryModal
        open={entryModalOpen}
        onClose={handleCloseEntryModal}
        onSubmit={handleEntrySubmit}
        loading={isCreating}
      />

      <InventarioExitModal
        open={exitModalOpen}
        onClose={handleCloseExitModal}
        onSubmit={handleExitSubmit}
        loading={false}
      />
    </div>
  );
};

export default InventarioPage;
