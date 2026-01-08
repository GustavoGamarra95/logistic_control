import { useState } from 'react';
import { Table, Button, Input, Space, Card, Tag, Select } from 'antd';
import { PlusOutlined, SearchOutlined, FileTextOutlined, QrcodeOutlined, EditOutlined, DeleteOutlined, SendOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { PageHeader } from '@/components/common/PageHeader';
import { useFacturas, useFactura } from '@/hooks/useFacturas';
import { usePagination } from '@/hooks/usePagination';
import { useDebounce } from '@/hooks/useDebounce';
import { Factura, EstadoFactura } from '@/types/factura.types';
import { FacturaFormModal } from '@/components/facturas/FacturaFormModal';
import { FacturaDetailView } from '@/components/facturas/FacturaDetailView';
import { FacturaFormData } from '@/schemas/factura.schema';
import { showConfirmModal } from '@/components/common/ConfirmModal';
import { formatCurrency, formatDate } from '@/utils/format';

const FacturasPage = () => {
  const [searchText, setSearchText] = useState('');
  const [estadoFilter, setEstadoFilter] = useState<EstadoFactura | 'TODOS'>('TODOS');
  const [modalOpen, setModalOpen] = useState(false);
  const [detailModalOpen, setDetailModalOpen] = useState(false);
  const [selectedFactura, setSelectedFactura] = useState<Factura | undefined>(undefined);

  const debouncedSearch = useDebounce(searchText, 300);
  const { page, size, handlePageChange, handleSizeChange } = usePagination();

  const {
    facturas,
    pagination,
    isLoading,
    createFactura,
    updateFactura,
    deleteFactura,
    enviarASifen,
    actualizarEstadoSifen,
    isCreating,
    isUpdating,
  } = useFacturas({
    page,
    size,
    search: debouncedSearch,
    estado: estadoFilter !== 'TODOS' ? estadoFilter : undefined,
  });

  const handleOpenModal = (factura?: Factura) => {
    setSelectedFactura(factura);
    setModalOpen(true);
  };

  const handleCloseModal = () => {
    setSelectedFactura(undefined);
    setModalOpen(false);
  };

  const handleOpenDetail = (factura: Factura) => {
    setSelectedFactura(factura);
    setDetailModalOpen(true);
  };

  const handleCloseDetail = () => {
    setSelectedFactura(undefined);
    setDetailModalOpen(false);
  };

  const handleSubmit = (data: FacturaFormData) => {
    if (selectedFactura) {
      updateFactura({ id: selectedFactura.id, data });
    } else {
      createFactura(data);
    }
  };

  const handleDelete = (factura: Factura) => {
    showConfirmModal({
      title: '¿Eliminar Factura?',
      content: `¿Está seguro de eliminar la factura ${factura.numeroFactura}? Esta acción no se puede deshacer.`,
      onConfirm: () => deleteFactura(factura.id),
      danger: true,
      okText: 'Eliminar',
    });
  };

  const handleEnviarSifen = (facturaId: number) => {
    showConfirmModal({
      title: 'Enviar a SIFEN',
      content: '¿Desea enviar esta factura al sistema SIFEN para su procesamiento?',
      onConfirm: () => enviarASifen({ id: facturaId }),
      okText: 'Enviar',
    });
  };

  const getEstadoColor = (estado: EstadoFactura): string => {
    const colors: Record<EstadoFactura, string> = {
      BORRADOR: 'default',
      EMITIDA: 'blue',
      ENVIADA_SIFEN: 'processing',
      APROBADA: 'success',
      RECHAZADA: 'error',
      ANULADA: 'default',
      PAGADA: 'success',
      VENCIDA: 'warning',
    };
    return colors[estado] || 'default';
  };

  const columns: ColumnsType<Factura> = [
    {
      title: 'Número',
      dataIndex: 'numeroFactura',
      key: 'numeroFactura',
      width: 150,
      fixed: 'left',
      render: (numero: string) => (
        <span className="font-mono font-semibold">{numero}</span>
      ),
    },
    {
      title: 'Cliente',
      dataIndex: 'clienteRazonSocial',
      key: 'clienteRazonSocial',
      ellipsis: true,
      width: 200,
    },
    {
      title: 'Fecha',
      dataIndex: 'fechaEmision',
      key: 'fechaEmision',
      width: 120,
      render: (fecha: string) => formatDate(fecha),
    },
    {
      title: 'Tipo',
      dataIndex: 'tipo',
      key: 'tipo',
      width: 100,
      render: (tipo: string) => <Tag>{tipo}</Tag>,
    },
    {
      title: 'Total',
      dataIndex: 'total',
      key: 'total',
      width: 150,
      align: 'right',
      render: (total: number, record) => formatCurrency(total, record.moneda),
    },
    {
      title: 'Estado',
      dataIndex: 'estado',
      key: 'estado',
      width: 130,
      render: (estado: EstadoFactura) => (
        <Tag color={getEstadoColor(estado)}>{estado}</Tag>
      ),
    },
    {
      title: 'SIFEN',
      dataIndex: 'estadoSifen',
      key: 'estadoSifen',
      width: 120,
      render: (estadoSifen?: string, record) => {
        if (!estadoSifen && (record.estado === 'BORRADOR' || record.estado === 'EMITIDA')) {
          return (
            <Button
              type="link"
              size="small"
              icon={<SendOutlined />}
              onClick={(e) => {
                e.stopPropagation();
                handleEnviarSifen(record.id);
              }}
            >
              Enviar
            </Button>
          );
        }
        return estadoSifen ? (
          <Tag color={record.estado === 'APROBADA' ? 'success' : 'processing'}>
            {estadoSifen}
          </Tag>
        ) : '-';
      },
    },
    {
      title: 'Pago',
      dataIndex: 'estadoPago',
      key: 'estadoPago',
      width: 100,
      render: (estadoPago: string) => {
        const colors: Record<string, string> = {
          PENDIENTE: 'orange',
          PARCIAL: 'blue',
          PAGADO: 'green',
          VENCIDO: 'red',
        };
        return <Tag color={colors[estadoPago] || 'default'}>{estadoPago}</Tag>;
      },
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
            icon={<FileTextOutlined />}
            size="small"
            onClick={() => handleOpenDetail(record)}
            title="Ver detalle"
          />
          {record.cdc && (
            <Button
              type="text"
              icon={<QrcodeOutlined />}
              size="small"
              onClick={() => handleOpenDetail(record)}
              title="Ver QR"
            />
          )}
          {(record.estado === 'BORRADOR' || record.estado === 'EMITIDA') && (
            <>
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
            </>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div>
      <PageHeader
        title="Gestión de Facturas"
        subtitle="Facturación electrónica con SIFEN"
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={() => handleOpenModal()}>
            Nueva Factura
          </Button>
        }
      />

      <Card>
        <Space vertical size="middle" className="w-full">
          <Space wrap>
            <Input
              placeholder="Buscar por número, cliente..."
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
                { value: 'BORRADOR', label: 'Borrador' },
                { value: 'EMITIDA', label: 'Emitida' },
                { value: 'ENVIADA_SIFEN', label: 'Enviada a SIFEN' },
                { value: 'APROBADA', label: 'Aprobada' },
                { value: 'RECHAZADA', label: 'Rechazada' },
                { value: 'ANULADA', label: 'Anulada' },
                { value: 'PAGADA', label: 'Pagada' },
                { value: 'VENCIDA', label: 'Vencida' },
              ]}
            />
          </Space>

          <Table
            columns={columns}
            dataSource={facturas}
            rowKey="id"
            loading={isLoading}
            pagination={{
              current: pagination.current,
              pageSize: pagination.pageSize,
              total: pagination.total,
              onChange: handlePageChange,
              onShowSizeChange: (_, size) => handleSizeChange(size),
              showSizeChanger: true,
              showTotal: (total) => `Total ${total} facturas`,
              pageSizeOptions: ['10', '20', '50', '100'],
            }}
            scroll={{ x: 1400 }}
          />
        </Space>
      </Card>

      <FacturaFormModal
        open={modalOpen}
        onClose={handleCloseModal}
        onSubmit={handleSubmit}
        initialData={selectedFactura}
        loading={isCreating || isUpdating}
      />

      <FacturaDetailView
        open={detailModalOpen}
        onClose={handleCloseDetail}
        factura={selectedFactura || null}
        items={selectedFactura?.items || []}
        onEnviarSifen={
          selectedFactura && (selectedFactura.estado === 'BORRADOR' || selectedFactura.estado === 'EMITIDA')
            ? () => enviarASifen({ id: selectedFactura.id })
            : undefined
        }
        onConsultarSifen={
          selectedFactura?.cdc
            ? () => actualizarEstadoSifen(selectedFactura.id)
            : undefined
        }
        onDescargarXML={
          selectedFactura?.cdc
            ? () => {
                window.open(`/api/facturas/${selectedFactura.id}/xml`, '_blank');
              }
            : undefined
        }
      />
    </div>
  );
};

export default FacturasPage;
