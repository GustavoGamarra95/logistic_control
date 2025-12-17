import { useState } from 'react';
import { Table, Button, Input, Space, Card, Tag, Switch } from 'antd';
import { PlusOutlined, SearchOutlined, EditOutlined, LockOutlined, UnlockOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { PageHeader } from '@/components/common/PageHeader';
import { useUsuarios } from '@/hooks/useUsuarios';
import { usePagination } from '@/hooks/usePagination';
import { useDebounce } from '@/hooks/useDebounce';
import { Usuario } from '@/types/usuario.types';

const UsuariosPage = () => {
  const [searchText, setSearchText] = useState('');

  const debouncedSearch = useDebounce(searchText, 300);
  const { page, size, handlePageChange, handleSizeChange } = usePagination();

  const {
    usuarios,
    pagination,
    isLoading,
    bloquearUsuario,
    desbloquearUsuario,
  } = useUsuarios({
    page,
    size,
    search: debouncedSearch,
  });

  const columns: ColumnsType<Usuario> = [
    {
      title: 'Username',
      dataIndex: 'username',
      key: 'username',
      width: 150,
    },
    {
      title: 'Nombre',
      dataIndex: 'nombre',
      key: 'nombre',
      ellipsis: true,
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      width: 200,
      ellipsis: true,
    },
    {
      title: 'Roles',
      dataIndex: 'roles',
      key: 'roles',
      width: 250,
      render: (roles: string[]) => (
        <Space size="small" wrap>
          {(roles || []).map(role => <Tag key={role} color="blue">{role}</Tag>)}
        </Space>
      ),
    },
    {
      title: 'Estado',
      key: 'estado',
      width: 150,
      render: (_, record) => (
        <Space>
          {record.bloqueado ? <Tag color="red">Bloqueado</Tag> : null}
          {!record.activo ? <Tag color="orange">Inactivo</Tag> : null}
          {record.activo && !record.bloqueado ? <Tag color="green">Activo</Tag> : null}
        </Space>
      ),
    },
    {
      title: 'Activo',
      dataIndex: 'activo',
      key: 'activo',
      width: 80,
      render: (activo: boolean) => <Switch checked={activo} size="small" />,
    },
    {
      title: 'Acciones',
      key: 'actions',
      width: 120,
      render: (_, record) => (
        <Space size="small">
          <Button type="text" icon={<EditOutlined />} size="small" title="Editar" />
          <Button
            type="text"
            icon={record.bloqueado ? <UnlockOutlined /> : <LockOutlined />}
            size="small"
            title={record.bloqueado ? 'Desbloquear' : 'Bloquear'}
          />
        </Space>
      ),
    },
  ];

  return (
    <div>
      <PageHeader
        title="Gestión de Usuarios"
        subtitle="Administración de usuarios y roles"
        extra={<Button type="primary" icon={<PlusOutlined />}>Nuevo Usuario</Button>}
      />
      <Card>
        <Space direction="vertical" size="middle" className="w-full">
          <Input
            placeholder="Buscar por nombre, username o email..."
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="max-w-md"
            allowClear
          />
          <Table
            columns={columns}
            dataSource={usuarios}
            rowKey="id"
            loading={isLoading}
            pagination={{
              current: pagination.current,
              pageSize: pagination.pageSize,
              total: pagination.total,
              showSizeChanger: true,
              showTotal: (total) => `Total ${total} usuarios`,
              pageSizeOptions: ['10', '20', '50', '100'],
              onChange: handlePageChange,
              onShowSizeChange: handleSizeChange,
            }}
            scroll={{ x: 1000 }}
          />
        </Space>
      </Card>
    </div>
  );
};

export default UsuariosPage;
