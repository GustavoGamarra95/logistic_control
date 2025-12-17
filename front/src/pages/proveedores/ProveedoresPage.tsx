import { useState } from 'react';
import { Table, Button, Input, Space, Card, Tag, Rate } from 'antd';
import { PlusOutlined, SearchOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { PageHeader } from '@/components/common/PageHeader';
import { DeleteConfirmModal } from '@/components/common/DeleteConfirmModal';
import { ProveedorFormModal } from '@/components/proveedores/ProveedorFormModal';
import { useProveedores } from '@/hooks/useProveedores';
import { usePagination } from '@/hooks/usePagination';
import { useDebounce } from '@/hooks/useDebounce';
import { Proveedor } from '@/types/proveedor.types';

const ProveedoresPage = () => {
  const [searchText, setSearchText] = useState('');
  const [formModalOpen, setFormModalOpen] = useState(false);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [selectedProveedor, setSelectedProveedor] = useState<Proveedor | undefined>();

  const debouncedSearch = useDebounce(searchText, 300);
  const { page, size, handlePageChange, handleSizeChange } = usePagination();

  const {
    proveedores,
    pagination,
    isLoading,
    createProveedor,
    updateProveedor,
    deleteProveedor,
    isCreating,
    isUpdating,
    isDeleting,
  } = useProveedores({
    page,
    size,
    search: debouncedSearch,
  });

  const handleCreate = () => {
    setSelectedProveedor(undefined);
    setFormModalOpen(true);
  };

  const handleEdit = (proveedor: Proveedor) => {
    setSelectedProveedor(proveedor);
    setFormModalOpen(true);
  };

  const handleDelete = (proveedor: Proveedor) => {
    setSelectedProveedor(proveedor);
    setDeleteModalOpen(true);
  };

  const handleFormSubmit = (data: any) => {
    if (selectedProveedor) {
      updateProveedor({ id: selectedProveedor.id, data });
      setFormModalOpen(false);
    } else {
      createProveedor(data);
      setFormModalOpen(false);
    }
  };

  const handleDeleteConfirm = (reason: string) => {
    if (selectedProveedor) {
      deleteProveedor({ id: selectedProveedor.id, reason });
      setDeleteModalOpen(false);
    }
  };

  const columns: ColumnsType<Proveedor> = [
    {
      title: 'RUC',
      dataIndex: 'ruc',
      key: 'ruc',
      width: 120,
    },
    {
      title: 'Razón Social',
      dataIndex: 'razonSocial',
      key: 'razonSocial',
      ellipsis: true,
    },
    {
      title: 'Nombre',
      dataIndex: 'nombre',
      key: 'nombre',
      ellipsis: true,
    },
    {
      title: 'Tipo',
      dataIndex: 'tipo',
      key: 'tipo',
      width: 150,
      render: (tipo: string) => <Tag color="blue">{tipo}</Tag>,
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      width: 200,
      ellipsis: true,
    },
    {
      title: 'Calificación',
      dataIndex: 'calificacion',
      key: 'calificacion',
      width: 150,
      render: (value?: number) => value ? <Rate disabled value={value} /> : '-',
    },
    {
      title: 'Acciones',
      key: 'actions',
      width: 120,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button type="text" icon={<EditOutlined />} size="small" onClick={() => handleEdit(record)} />
          <Button type="text" danger icon={<DeleteOutlined />} size="small" onClick={() => handleDelete(record)} />
        </Space>
      ),
    },
  ];

  return (
    <div>
      <PageHeader
        title="Gestión de Proveedores"
        subtitle="Administra transportistas, aduaneros y otros proveedores"
        extra={<Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>Nuevo Proveedor</Button>}
      />
      <Card>
        <Space direction="vertical" size="middle" className="w-full">
          <Input
            placeholder="Buscar por nombre, RUC o tipo..."
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="max-w-md"
            allowClear
          />
          <Table
            columns={columns}
            dataSource={proveedores}
            rowKey="id"
            loading={isLoading}
            pagination={{
              current: pagination.current,
              pageSize: pagination.pageSize,
              total: pagination.total,
              showSizeChanger: true,
              showTotal: (total) => `Total ${total} proveedores`,
              pageSizeOptions: ['10', '20', '50', '100'],
              onChange: handlePageChange,
              onShowSizeChange: handleSizeChange,
            }}
            scroll={{ x: 1000 }}
          />
        </Space>
      </Card>

      <ProveedorFormModal
        open={formModalOpen}
        onClose={() => setFormModalOpen(false)}
        onSubmit={handleFormSubmit}
        initialData={selectedProveedor}
        loading={isCreating || isUpdating}
      />

      <DeleteConfirmModal
        visible={deleteModalOpen}
        onCancel={() => setDeleteModalOpen(false)}
        onConfirm={handleDeleteConfirm}
        loading={isDeleting}
        content="¿Está seguro de que desea eliminar este proveedor?"
        itemName={selectedProveedor?.razonSocial || ''}
      />
    </div>
  );
};

export default ProveedoresPage;
