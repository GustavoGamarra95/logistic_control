import { useState } from 'react';
import { Table, Button, Input, Space, Card, Tag, Progress } from 'antd';
import { PlusOutlined, SearchOutlined, EditOutlined, DeleteOutlined, EnvironmentOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { Contenedor, EstadoContenedor } from '@/types/contenedor.types';
import { useContainers } from '@/hooks/useContainers';
import { usePagination } from '@/hooks/usePagination';
import { useDebounce } from '@/hooks/useDebounce';
import { PageHeader } from '@/components/common/PageHeader';
import { showConfirmModal } from '@/components/common/ConfirmModal';
import { ContenedorFormModal } from '@/components/contenedores/ContenedorFormModal';

const ContenedoresPage = () => {
  const [searchText, setSearchText] = useState('');
  const [formModalOpen, setFormModalOpen] = useState(false);
  const [selectedContenedor, setSelectedContenedor] = useState<Contenedor | undefined>();

  const debouncedSearch = useDebounce(searchText, 300);
  const { page, size, handlePageChange, handleSizeChange } = usePagination();

  const {
    containers: contenedores,
    pagination,
    isLoading,
    createContainer,
    updateContainer,
    isCreating,
    isUpdating,
  } = useContainers({
    page,
    size,
    search: debouncedSearch,
  });

  const handleCreate = () => {
    setSelectedContenedor(undefined);
    setFormModalOpen(true);
  };

  const handleEdit = (contenedor: Contenedor) => {
    setSelectedContenedor(contenedor);
    setFormModalOpen(true);
  };

  const handleDelete = (contenedor: Contenedor) => {
    showConfirmModal({
      title: '¿Eliminar Contenedor?',
      content: `¿Está seguro de eliminar el contenedor ${contenedor.numeroContenedor}? Esta acción no se puede deshacer.`,
      onConfirm: () => console.log('Delete', contenedor.id),
      danger: true,
      okText: 'Eliminar',
    });
  };

  const handleFormSubmit = (data: any) => {
    if (selectedContenedor) {
      updateContainer({ id: selectedContenedor.id, data });
    } else {
      createContainer(data);
    }
  };

  const getEstadoColor = (estado: EstadoContenedor): string => {
    const colors: Record<EstadoContenedor, string> = {
      DISPONIBLE: 'green',
      EN_TRANSITO: 'blue',
      EN_PUERTO: 'cyan',
      EN_DEPOSITO: 'purple',
      EN_CONSOLIDACION: 'orange',
      CERRADO: 'gold',
      DESPACHADO: 'default',
    };
    return colors[estado];
  };

  const columns: ColumnsType<Contenedor> = [
    {
      title: 'Número',
      dataIndex: 'numeroContenedor',
      key: 'numeroContenedor',
      width: 150,
      fixed: 'left',
    },
    {
      title: 'Tipo',
      dataIndex: 'tipo',
      key: 'tipo',
      width: 100,
      render: (tipo: string) => <Tag color="blue">{tipo}</Tag>,
    },
    {
      title: 'Estado',
      dataIndex: 'estado',
      key: 'estado',
      width: 150,
      render: (estado: EstadoContenedor) => (
        <Tag color={getEstadoColor(estado)}>{estado?.replace(/_/g, ' ') || 'N/A'}</Tag>
      ),
    },
    {
      title: 'Ocupación Peso',
      key: 'ocupacionPeso',
      width: 180,
      render: (_, record) => (
        <div>
          <Progress
            percent={record.porcentajeOcupacionPeso || 0}
            size="small"
            status={(record.porcentajeOcupacionPeso || 0) > 90 ? 'exception' : 'normal'}
          />
          <small className="text-gray-500">
            {(record.pesoActualKg || 0).toFixed(0)} / {(record.capacidadKg || 0).toFixed(0)} kg
          </small>
        </div>
      ),
    },
    {
      title: 'Ocupación Volumen',
      key: 'ocupacionVolumen',
      width: 180,
      render: (_, record) => (
        <div>
          <Progress
            percent={record.porcentajeOcupacionVolumen || 0}
            size="small"
            status={(record.porcentajeOcupacionVolumen || 0) > 90 ? 'exception' : 'normal'}
          />
          <small className="text-gray-500">
            {(record.volumenActualM3 || 0).toFixed(2)} / {(record.capacidadM3 || 0).toFixed(2)} m³
          </small>
        </div>
      ),
    },
    {
      title: 'Ubicación',
      dataIndex: 'ubicacionActual',
      key: 'ubicacionActual',
      width: 150,
      ellipsis: true,
    },
    {
      title: 'Puerto Origen',
      dataIndex: 'puertoOrigen',
      key: 'puertoOrigen',
      width: 130,
    },
    {
      title: 'Puerto Destino',
      dataIndex: 'puertoDestino',
      key: 'puertoDestino',
      width: 130,
    },
    {
      title: 'Acciones',
      key: 'actions',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="text"
            icon={<EnvironmentOutlined />}
            size="small"
            title="Ver en mapa"
          />
          <Button
            type="text"
            icon={<EditOutlined />}
            size="small"
            onClick={() => handleEdit(record)}
          />
          <Button
            type="text"
            danger
            icon={<DeleteOutlined />}
            size="small"
            onClick={() => handleDelete(record)}
          />
        </Space>
      ),
    },
  ];

  return (
    <div>
      <PageHeader
        title="Gestión de Contenedores"
        subtitle="Administra contenedores y su consolidación"
        extra={
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleCreate}
          >
            Nuevo Contenedor
          </Button>
        }
      />

      <Card>
        <Space direction="vertical" size="middle" className="w-full">
          <Input
            placeholder="Buscar por número de contenedor..."
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="max-w-md"
            allowClear
          />

          <Table
            columns={columns}
            dataSource={contenedores}
            rowKey="id"
            loading={isLoading}
            pagination={{
              current: pagination.current,
              pageSize: pagination.pageSize,
              total: pagination.total,
              showSizeChanger: true,
              showTotal: (total) => `Total ${total} contenedores`,
              pageSizeOptions: ['10', '20', '50', '100'],
              onChange: handlePageChange,
              onShowSizeChange: handleSizeChange,
            }}
            scroll={{ x: 1400 }}
          />
        </Space>
      </Card>

      <ContenedorFormModal
        open={formModalOpen}
        onClose={() => {
          setFormModalOpen(false);
          setSelectedContenedor(undefined);
        }}
        onSubmit={handleFormSubmit}
        initialData={selectedContenedor}
        loading={isCreating || isUpdating}
      />
    </div>
  );
};

export default ContenedoresPage;
