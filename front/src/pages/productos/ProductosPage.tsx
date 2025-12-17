import { useState } from 'react';
import { Table, Button, Input, Space, Card, Tag } from 'antd';
import { PlusOutlined, SearchOutlined, EditOutlined, DeleteOutlined, WarningOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { Producto, TipoProducto } from '@/types/producto.types';
import { useProductos } from '@/hooks/useProductos';
import { usePagination } from '@/hooks/usePagination';
import { useDebounce } from '@/hooks/useDebounce';
import { PageHeader } from '@/components/common/PageHeader';
import { showConfirmModal } from '@/components/common/ConfirmModal';
import { ProductoFormModal } from '@/components/productos/ProductoFormModal';

const ProductosPage = () => {
  const [searchText, setSearchText] = useState('');
  const [formModalOpen, setFormModalOpen] = useState(false);
  const [selectedProducto, setSelectedProducto] = useState<Producto | undefined>();

  const debouncedSearch = useDebounce(searchText, 300);
  const { page, size, handlePageChange, handleSizeChange } = usePagination();

  const {
    productos,
    pagination,
    isLoading,
    createProducto,
    updateProducto,
    deleteProducto,
    isCreating,
    isUpdating,
  } = useProductos({
    page,
    size,
    search: debouncedSearch,
  });

  const handleCreate = () => {
    setSelectedProducto(undefined);
    setFormModalOpen(true);
  };

  const handleEdit = (producto: Producto) => {
    setSelectedProducto(producto);
    setFormModalOpen(true);
  };

  const handleDelete = (producto: Producto) => {
    showConfirmModal({
      title: '¿Eliminar Producto?',
      content: `¿Está seguro de eliminar el producto ${producto.descripcion}? Esta acción no se puede deshacer.`,
      onConfirm: () => deleteProducto(producto.id),
      danger: true,
      okText: 'Eliminar',
    });
  };

  const handleFormSubmit = (data: any) => {
    if (selectedProducto) {
      updateProducto({ id: selectedProducto.id, data });
    } else {
      createProducto(data);
    }
  };

  const getTipoColor = (tipo: TipoProducto): string => {
    const colors: Record<TipoProducto, string> = {
      GENERAL: 'default',
      PELIGROSO: 'red',
      PERECEDERO: 'orange',
      FRAGIL: 'gold',
      REFRIGERADO: 'cyan',
    };
    return colors[tipo];
  };

  const columns: ColumnsType<Producto> = [
    {
      title: 'Código',
      dataIndex: 'codigo',
      key: 'codigo',
      width: 120,
      fixed: 'left',
    },
    {
      title: 'Descripción',
      dataIndex: 'descripcion',
      key: 'descripcion',
      width: 250,
      ellipsis: true,
    },
    {
      title: 'NCM/Arancel',
      dataIndex: 'ncmArancel',
      key: 'ncmArancel',
      width: 120,
    },
    {
      title: 'Tipo',
      dataIndex: 'tipo',
      key: 'tipo',
      width: 120,
      render: (tipo: TipoProducto) => (
        <Tag color={getTipoColor(tipo)}>{tipo}</Tag>
      ),
    },
    {
      title: 'Características',
      key: 'caracteristicas',
      width: 200,
      render: (_, record) => (
        <Space size="small" wrap>
          {record.esPeligroso && <Tag color="red" icon={<WarningOutlined />}>Peligroso</Tag>}
          {record.esPerecedero && <Tag color="orange">Perecedero</Tag>}
          {record.esFragil && <Tag color="gold">Frágil</Tag>}
          {record.requiereRefrigeracion && <Tag color="cyan">Refrigerado</Tag>}
        </Space>
      ),
    },
    {
      title: 'Peso Unitario (kg)',
      dataIndex: 'pesoUnitarioKg',
      key: 'pesoUnitarioKg',
      width: 140,
      align: 'right',
      render: (value: number) => (value || 0).toFixed(2),
    },
    {
      title: 'Volumen (m³)',
      dataIndex: 'volumenUnitarioM3',
      key: 'volumenUnitarioM3',
      width: 120,
      align: 'right',
      render: (value: number) => (value || 0).toFixed(3),
    },
    {
      title: 'Valor Unitario',
      key: 'valorUnitario',
      width: 140,
      align: 'right',
      render: (_, record) =>
        new Intl.NumberFormat('es-PY', {
          style: 'currency',
          currency: record.moneda || 'PYG',
          minimumFractionDigits: 0,
        }).format(record.valorUnitario || 0),
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
        title="Gestión de Productos"
        subtitle="Administra el catálogo de productos"
        extra={
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleCreate}
          >
            Nuevo Producto
          </Button>
        }
      />

      <Card>
        <Space direction="vertical" size="middle" className="w-full">
          <Input
            placeholder="Buscar por código, descripción o NCM..."
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="max-w-md"
            allowClear
          />

          <Table
            columns={columns}
            dataSource={productos}
            rowKey="id"
            loading={isLoading}
            pagination={{
              current: pagination.current,
              pageSize: pagination.pageSize,
              total: pagination.total,
              showSizeChanger: true,
              showTotal: (total) => `Total ${total} productos`,
              pageSizeOptions: ['10', '20', '50', '100'],
              onChange: handlePageChange,
              onShowSizeChange: handleSizeChange,
            }}
            scroll={{ x: 1500 }}
          />
        </Space>
      </Card>

      <ProductoFormModal
        open={formModalOpen}
        onClose={() => {
          setFormModalOpen(false);
          setSelectedProducto(undefined);
        }}
        onSubmit={handleFormSubmit}
        initialData={selectedProducto}
        loading={isCreating || isUpdating}
      />
    </div>
  );
};

export default ProductosPage;
