import { Modal, Form, Input, Select, InputNumber, Row, Col, Table, Button, Space, DatePicker, Divider } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import { useForm, Controller, useFieldArray } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { facturaSchema, FacturaFormData, ItemFacturaFormData } from '@/schemas/factura.schema';
import { Factura, TipoFactura } from '@/types/factura.types';
import { useClientes } from '@/hooks/useClientes';
import { usePedidos } from '@/hooks/usePedidos';
import { useState, useEffect, useMemo } from 'react';
import dayjs from 'dayjs';
import type { ColumnsType } from 'antd/es/table';

interface FacturaFormModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: FacturaFormData) => void;
  initialData?: Factura;
  loading?: boolean;
}

const MONEDAS = [
  { value: 'PYG', label: 'PYG - Guaraníes' },
  { value: 'USD', label: 'USD - Dólares' },
  { value: 'EUR', label: 'EUR - Euros' },
];

const UNIDADES_MEDIDA = [
  { value: 'UNI', label: 'Unidad' },
  { value: 'KG', label: 'Kilogramo' },
  { value: 'LT', label: 'Litro' },
  { value: 'MT', label: 'Metro' },
  { value: 'M2', label: 'Metro Cuadrado' },
  { value: 'M3', label: 'Metro Cúbico' },
  { value: 'CJ', label: 'Caja' },
  { value: 'PZ', label: 'Pieza' },
];

const TASAS_IVA = [
  { value: 0, label: 'Exento (0%)' },
  { value: 5, label: 'IVA 5%' },
  { value: 10, label: 'IVA 10%' },
];

export const FacturaFormModal = ({
  open,
  onClose,
  onSubmit,
  initialData,
  loading = false,
}: FacturaFormModalProps) => {
  const [tipoFactura, setTipoFactura] = useState<TipoFactura>('CONTADO');

  // Obtener clientes y pedidos
  const { clientes, isLoading: loadingClientes } = useClientes({ page: 0, size: 100 });
  const { pedidos, isLoading: loadingPedidos } = usePedidos({ page: 0, size: 100 });

  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
    watch,
    setValue,
  } = useForm<FacturaFormData>({
    resolver: zodResolver(facturaSchema),
    defaultValues: initialData
      ? {
          ...initialData,
          fechaEmision: initialData.fechaEmision
            ? dayjs(initialData.fechaEmision).format('YYYY-MM-DD')
            : dayjs().format('YYYY-MM-DD'),
          fechaVencimiento: initialData.fechaVencimiento
            ? dayjs(initialData.fechaVencimiento).format('YYYY-MM-DD')
            : undefined,
          items: [],
        }
      : {
          clienteId: 0,
          timbrado: '',
          puntoExpedicion: '001',
          establecimiento: '001',
          tipo: 'CONTADO',
          fechaEmision: dayjs().format('YYYY-MM-DD'),
          moneda: 'PYG',
          items: [],
        },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: 'items',
  });

  // Watch items para calcular totales
  const items = watch('items');
  const tipo = watch('tipo');

  useEffect(() => {
    setTipoFactura(tipo);
  }, [tipo]);

  // Calcular totales
  const totales = useMemo(() => {
    let subtotal = 0;
    let iva5 = 0;
    let iva10 = 0;

    items?.forEach((item) => {
      const itemSubtotal = item.cantidad * item.precioUnitario;
      subtotal += itemSubtotal;

      if (item.tasaIva === 5) {
        iva5 += itemSubtotal * 0.05;
      } else if (item.tasaIva === 10) {
        iva10 += itemSubtotal * 0.10;
      }
    });

    const ivaTotal = iva5 + iva10;
    const total = subtotal + ivaTotal;

    return {
      subtotal,
      iva5,
      iva10,
      ivaTotal,
      total,
    };
  }, [items]);

  const handleClose = () => {
    reset();
    setTipoFactura('CONTADO');
    onClose();
  };

  const onFormSubmit = (data: FacturaFormData) => {
    onSubmit(data);
    handleClose();
  };

  const handleAddItem = () => {
    append({
      codigo: '',
      descripcion: '',
      cantidad: 1,
      unidadMedida: 'UNI',
      precioUnitario: 0,
      tasaIva: 10,
    });
  };

  const itemsColumns: ColumnsType<ItemFacturaFormData & { id: string }> = [
    {
      title: 'Código',
      dataIndex: 'codigo',
      key: 'codigo',
      width: 100,
      render: (_, __, index) => (
        <Controller
          name={`items.${index}.codigo`}
          control={control}
          render={({ field }) => (
            <Input {...field} placeholder="Código" size="small" />
          )}
        />
      ),
    },
    {
      title: 'Descripción',
      dataIndex: 'descripcion',
      key: 'descripcion',
      width: 250,
      render: (_, __, index) => (
        <Controller
          name={`items.${index}.descripcion`}
          control={control}
          render={({ field }) => (
            <Input {...field} placeholder="Descripción del ítem" size="small" status={errors.items?.[index]?.descripcion ? 'error' : ''} />
          )}
        />
      ),
    },
    {
      title: 'Cantidad',
      dataIndex: 'cantidad',
      key: 'cantidad',
      width: 100,
      render: (_, __, index) => (
        <Controller
          name={`items.${index}.cantidad`}
          control={control}
          render={({ field }) => (
            <InputNumber {...field} min={1} size="small" className="w-full" status={errors.items?.[index]?.cantidad ? 'error' : ''} />
          )}
        />
      ),
    },
    {
      title: 'Unidad',
      dataIndex: 'unidadMedida',
      key: 'unidadMedida',
      width: 100,
      render: (_, __, index) => (
        <Controller
          name={`items.${index}.unidadMedida`}
          control={control}
          render={({ field }) => (
            <Select {...field} options={UNIDADES_MEDIDA} size="small" className="w-full" />
          )}
        />
      ),
    },
    {
      title: 'Precio Unit.',
      dataIndex: 'precioUnitario',
      key: 'precioUnitario',
      width: 120,
      render: (_, __, index) => (
        <Controller
          name={`items.${index}.precioUnitario`}
          control={control}
          render={({ field }) => (
            <InputNumber
              {...field}
              min={0}
              size="small"
              className="w-full"
              formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
              parser={(value) => value!.replace(/,/g, '')}
              status={errors.items?.[index]?.precioUnitario ? 'error' : ''}
            />
          )}
        />
      ),
    },
    {
      title: 'IVA',
      dataIndex: 'tasaIva',
      key: 'tasaIva',
      width: 100,
      render: (_, __, index) => (
        <Controller
          name={`items.${index}.tasaIva`}
          control={control}
          render={({ field }) => (
            <Select {...field} options={TASAS_IVA} size="small" className="w-full" />
          )}
        />
      ),
    },
    {
      title: 'Subtotal',
      key: 'subtotal',
      width: 120,
      align: 'right',
      render: (_, __, index) => {
        const item = items?.[index];
        if (!item) return 0;
        const subtotal = item.cantidad * item.precioUnitario;
        return new Intl.NumberFormat('es-PY').format(subtotal);
      },
    },
    {
      title: '',
      key: 'actions',
      width: 50,
      render: (_, __, index) => (
        <Button
          type="text"
          danger
          icon={<DeleteOutlined />}
          size="small"
          onClick={() => remove(index)}
          title="Eliminar ítem"
        />
      ),
    },
  ];

  return (
    <Modal
      title={initialData ? 'Editar Factura' : 'Nueva Factura'}
      open={open}
      onCancel={handleClose}
      onOk={handleSubmit(onFormSubmit)}
      okText={initialData ? 'Actualizar' : 'Crear'}
      cancelText="Cancelar"
      width={1200}
      confirmLoading={loading}
    >
      <Form layout="vertical" className="mt-4">
        {/* Información Principal */}
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              label="Cliente"
              validateStatus={errors.clienteId ? 'error' : ''}
              help={errors.clienteId?.message}
              required
            >
              <Controller
                name="clienteId"
                control={control}
                render={({ field }) => (
                  <Select
                    {...field}
                    placeholder="Seleccione el cliente"
                    loading={loadingClientes}
                    showSearch
                    filterOption={(input, option) =>
                      (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
                    }
                    options={clientes?.map((cliente) => ({
                      value: cliente.id,
                      label: `${cliente.razonSocial} - ${cliente.ruc}`,
                    }))}
                  />
                )}
              />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item label="Pedido Relacionado">
              <Controller
                name="pedidoId"
                control={control}
                render={({ field }) => (
                  <Select
                    {...field}
                    placeholder="Seleccione el pedido (opcional)"
                    loading={loadingPedidos}
                    showSearch
                    allowClear
                    filterOption={(input, option) =>
                      (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
                    }
                    options={pedidos?.map((pedido) => ({
                      value: pedido.id,
                      label: `${pedido.codigoTracking} - ${pedido.descripcionMercaderia}`,
                    }))}
                  />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={6}>
            <Form.Item
              label="Timbrado"
              validateStatus={errors.timbrado ? 'error' : ''}
              help={errors.timbrado?.message}
              required
            >
              <Controller
                name="timbrado"
                control={control}
                render={({ field }) => <Input {...field} placeholder="12345678" maxLength={20} />}
              />
            </Form.Item>
          </Col>

          <Col span={6}>
            <Form.Item
              label="Establecimiento"
              validateStatus={errors.establecimiento ? 'error' : ''}
              help={errors.establecimiento?.message}
              required
            >
              <Controller
                name="establecimiento"
                control={control}
                render={({ field }) => <Input {...field} placeholder="001" maxLength={10} />}
              />
            </Form.Item>
          </Col>

          <Col span={6}>
            <Form.Item
              label="Punto Expedición"
              validateStatus={errors.puntoExpedicion ? 'error' : ''}
              help={errors.puntoExpedicion?.message}
              required
            >
              <Controller
                name="puntoExpedicion"
                control={control}
                render={({ field }) => <Input {...field} placeholder="001" maxLength={10} />}
              />
            </Form.Item>
          </Col>

          <Col span={6}>
            <Form.Item
              label="Tipo"
              validateStatus={errors.tipo ? 'error' : ''}
              help={errors.tipo?.message}
              required
            >
              <Controller
                name="tipo"
                control={control}
                render={({ field }) => (
                  <Select
                    {...field}
                    options={[
                      { value: 'CONTADO', label: 'Contado' },
                      { value: 'CREDITO', label: 'Crédito' },
                    ]}
                  />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={6}>
            <Form.Item label="Fecha Emisión" validateStatus={errors.fechaEmision ? 'error' : ''} help={errors.fechaEmision?.message}>
              <Controller
                name="fechaEmision"
                control={control}
                render={({ field }) => (
                  <DatePicker
                    {...field}
                    value={field.value ? dayjs(field.value) : dayjs()}
                    onChange={(date) => field.onChange(date ? date.format('YYYY-MM-DD') : null)}
                    format="DD/MM/YYYY"
                    placeholder="Seleccione fecha"
                    className="w-full"
                  />
                )}
              />
            </Form.Item>
          </Col>

          {tipoFactura === 'CREDITO' && (
            <Col span={6}>
              <Form.Item label="Fecha Vencimiento" validateStatus={errors.fechaVencimiento ? 'error' : ''} help={errors.fechaVencimiento?.message}>
                <Controller
                  name="fechaVencimiento"
                  control={control}
                  render={({ field }) => (
                    <DatePicker
                      {...field}
                      value={field.value ? dayjs(field.value) : null}
                      onChange={(date) => field.onChange(date ? date.format('YYYY-MM-DD') : null)}
                      format="DD/MM/YYYY"
                      placeholder="Seleccione fecha"
                      className="w-full"
                    />
                  )}
                />
              </Form.Item>
            </Col>
          )}

          <Col span={6}>
            <Form.Item
              label="Moneda"
              validateStatus={errors.moneda ? 'error' : ''}
              help={errors.moneda?.message}
              required
            >
              <Controller
                name="moneda"
                control={control}
                render={({ field }) => <Select {...field} options={MONEDAS} />}
              />
            </Form.Item>
          </Col>

          <Col span={tipoFactura === 'CREDITO' ? 6 : 12}>
            <Form.Item label="Condición de Pago" validateStatus={errors.condicionPago ? 'error' : ''} help={errors.condicionPago?.message}>
              <Controller
                name="condicionPago"
                control={control}
                render={({ field }) => <Input {...field} placeholder="Ej: 30 días" maxLength={200} />}
              />
            </Form.Item>
          </Col>
        </Row>

        <Divider>Ítems de la Factura</Divider>

        {/* Tabla de Ítems */}
        <div className="mb-4">
          <Button
            type="dashed"
            onClick={handleAddItem}
            icon={<PlusOutlined />}
            className="mb-2"
            block
          >
            Agregar Ítem
          </Button>

          {errors.items && !Array.isArray(errors.items) && (
            <div className="text-red-500 text-sm mb-2">{errors.items.message}</div>
          )}

          <Table
            columns={itemsColumns}
            dataSource={fields}
            pagination={false}
            size="small"
            scroll={{ x: 900 }}
            locale={{ emptyText: 'No hay ítems. Agregue ítems a la factura' }}
          />
        </div>

        {/* Resumen de Totales */}
        {items && items.length > 0 && (
          <div className="bg-gray-50 p-4 rounded">
            <Row gutter={16}>
              <Col span={18}>
                <Form.Item label="Observaciones">
                  <Controller
                    name="observaciones"
                    control={control}
                    render={({ field }) => (
                      <Input.TextArea {...field} rows={3} placeholder="Observaciones adicionales..." maxLength={1000} showCount />
                    )}
                  />
                </Form.Item>
              </Col>

              <Col span={6}>
                <div className="space-y-2">
                  <div className="flex justify-between">
                    <span className="font-medium">Subtotal:</span>
                    <span>{new Intl.NumberFormat('es-PY').format(totales.subtotal)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm text-gray-600">IVA 5%:</span>
                    <span className="text-sm">{new Intl.NumberFormat('es-PY').format(totales.iva5)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm text-gray-600">IVA 10%:</span>
                    <span className="text-sm">{new Intl.NumberFormat('es-PY').format(totales.iva10)}</span>
                  </div>
                  <div className="flex justify-between border-t pt-2">
                    <span className="font-bold">Total:</span>
                    <span className="font-bold text-lg">{new Intl.NumberFormat('es-PY').format(totales.total)}</span>
                  </div>
                </div>
              </Col>
            </Row>
          </div>
        )}
      </Form>
    </Modal>
  );
};
