import { useState } from 'react';
import { Table, Button, Input, Space, Card, Tag } from 'antd';
import { PlusOutlined, SearchOutlined, EditOutlined, DeleteOutlined, DollarOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useClientes } from '@/hooks/useClientes';
import { usePagination } from '@/hooks/usePagination';
import { useDebounce } from '@/hooks/useDebounce';
import { Cliente } from '@/types/cliente.types';
import { ClienteFormModal } from '@/components/clientes/ClienteFormModal';
import { CreditoModal } from '@/components/clientes/CreditoModal';
import { PageHeader } from '@/components/common/PageHeader';
import { showConfirmModal } from '@/components/common/ConfirmModal';
import { ClienteFormData } from '@/schemas/cliente.schema';

const ClientesPage = () => {
  const [searchText, setSearchText] = useState('');
  const [formModalOpen, setFormModalOpen] = useState(false);
  const [creditoModalOpen, setCreditoModalOpen] = useState(false);
  const [selectedCliente, setSelectedCliente] = useState<Cliente | undefined>();
  
  const debouncedSearch = useDebounce(searchText, 300);
  const { page, size, handlePageChange, handleSizeChange } = usePagination();

  const { 
    clientes, 
    pagination, 
    isLoading, 
    createCliente,
    updateCliente,
    deleteCliente,
    updateCredito,
    isCreating,
    isUpdating,
  } = useClientes({
    page,
    size,
    search: debouncedSearch,
  });

  const handleCreate = () => {
    setSelectedCliente(undefined);
    setFormModalOpen(true);
  };

  const handleEdit = (cliente: Cliente) => {
    setSelectedCliente(cliente);
    setFormModalOpen(true);
  };

  const handleDelete = (cliente: Cliente) => {
    showConfirmModal({
      title: '¿Eliminar Cliente?',
      content: `¿Está seguro de eliminar a ${cliente.razonSocial}? Esta acción no se puede deshacer.`,
      onConfirm: () => deleteCliente(cliente.id),
      danger: true,
      okText: 'Eliminar',
    });
  };

  const handleCredito = (cliente: Cliente) => {
    setSelectedCliente(cliente);
    setCreditoModalOpen(true);
  };

  const handleFormSubmit = (data: ClienteFormData) => {
    if (selectedCliente) {
      updateCliente({ id: selectedCliente.id, data });
    } else {
      createCliente(data as any);
    }
  };

  const handleCreditoSubmit = (monto: number) => {
    if (selectedCliente) {
      updateCredito({ id: selectedCliente.id, monto });
    }
  };

  const columns: ColumnsType<Cliente> = [
    {
      title: 'RUC',
      dataIndex: 'ruc',
      key: 'ruc',
      width: 120,
      render: (ruc: string, record) => `${ruc}-${record.dv}`,
    },
    {
      title: 'Razón Social',
      dataIndex: 'razonSocial',
      key: 'razonSocial',
      ellipsis: true,
    },
    {
      title: 'Nombre Fantasía',
      dataIndex: 'nombreFantasia',
      key: 'nombreFantasia',
      ellipsis: true,
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      ellipsis: true,
    },
    {
      title: 'Ciudad',
      dataIndex: 'ciudad',
      key: 'ciudad',
      width: 120,
    },
    {
      title: 'Tipo Servicio',
      dataIndex: 'tipoServicio',
      key: 'tipoServicio',
      width: 130,
      render: (tipo: string) => {
        const colors: Record<string, string> = {
          AEREO: 'blue',
          MARITIMO: 'cyan',
          TERRESTRE: 'green',
          MULTIMODAL: 'purple',
        };
        return <Tag color={colors[tipo] || 'default'}>{tipo}</Tag>;
      },
    },
    {
      title: 'Crédito Disponible',
      dataIndex: 'creditoDisponible',
      key: 'creditoDisponible',
      width: 150,
      align: 'right',
      render: (value: number) =>
        new Intl.NumberFormat('es-PY', {
          style: 'currency',
          currency: 'PYG',
          minimumFractionDigits: 0,
        }).format(value || 0),
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
            icon={<DollarOutlined />}
            size="small"
            onClick={() => handleCredito(record)}
            title="Gestionar crédito"
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
        title="Gestión de Clientes"
        subtitle="Administra la información de tus clientes"
        extra={
          <Button 
            type="primary" 
            icon={<PlusOutlined />}
            onClick={handleCreate}
          >
            Nuevo Cliente
          </Button>
        }
      />

      <Card>
        <Space vertical size="middle" className="w-full">
          <Input
            placeholder="Buscar por razón social, RUC o email..."
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="max-w-md"
            allowClear
          />

          <Table
            columns={columns}
            dataSource={clientes}
            rowKey="id"
            loading={isLoading}
            pagination={{
              current: pagination.current,
              pageSize: pagination.pageSize,
              total: pagination.total,
              onChange: handlePageChange,
              onShowSizeChange: (_, size) => handleSizeChange(size),
              showSizeChanger: true,
              showTotal: (total) => `Total ${total} clientes`,
              pageSizeOptions: ['10', '20', '50', '100'],
            }}
            scroll={{ x: 1200 }}
          />
        </Space>
      </Card>

      <ClienteFormModal
        open={formModalOpen}
        onClose={() => setFormModalOpen(false)}
        onSubmit={handleFormSubmit}
        initialData={selectedCliente}
        loading={isCreating || isUpdating}
      />

      <CreditoModal
        open={creditoModalOpen}
        onClose={() => setCreditoModalOpen(false)}
        onSubmit={handleCreditoSubmit}
        currentCredito={selectedCliente?.creditoLimite || 0}
      />
    </div>
  );
};

export default ClientesPage;
