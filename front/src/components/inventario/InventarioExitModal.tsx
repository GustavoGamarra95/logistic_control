import { Modal, Form, Input, Select, InputNumber, Row, Col, Alert } from 'antd';
import { useForm, Controller } from 'react-hook-form';
import { CreateMovimientoRequest, TipoMovimiento } from '@/types/inventario.types';
import { useInventario } from '@/hooks/useInventario';
import { useProductos } from '@/hooks/useProductos';
import { usePedidos } from '@/hooks/usePedidos';
import { useState, useMemo } from 'react';

interface InventarioExitModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: CreateMovimientoRequest) => void;
  loading?: boolean;
}

const TIPOS_MOVIMIENTO: { value: TipoMovimiento; label: string; description: string }[] = [
  { value: 'SALIDA', label: 'Salida', description: 'Salida definitiva del inventario' },
  { value: 'RESERVA', label: 'Reserva', description: 'Reservar para un pedido' },
  { value: 'TRANSFERENCIA', label: 'Transferencia', description: 'Transferir a otro almacén' },
  { value: 'AJUSTE', label: 'Ajuste', description: 'Ajuste de inventario (pérdida, daño, etc.)' },
];

export const InventarioExitModal = ({
  open,
  onClose,
  onSubmit,
  loading = false,
}: InventarioExitModalProps) => {
  const [selectedProducto, setSelectedProducto] = useState<number | null>(null);
  const [selectedInventario, setSelectedInventario] = useState<number | null>(null);

  // Obtener productos y pedidos
  const { productos, isLoading: loadingProductos } = useProductos({ page: 0, size: 100 });
  const { pedidos, isLoading: loadingPedidos } = usePedidos({ page: 0, size: 100 });

  // Obtener inventarios del producto seleccionado
  const { inventarios, isLoading: loadingInventarios } = useInventario({
    productoId: selectedProducto || undefined,
    estado: 'DISPONIBLE',
  });

  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
    watch,
    setValue,
  } = useForm<CreateMovimientoRequest>({
    defaultValues: {
      inventarioId: 0,
      tipoMovimiento: 'SALIDA',
      cantidad: 0,
      referencia: '',
      observaciones: '',
    },
  });

  const inventarioId = watch('inventarioId');
  const tipoMovimiento = watch('tipoMovimiento');
  const cantidad = watch('cantidad');

  // Obtener información del inventario seleccionado
  const inventarioSeleccionado = useMemo(() => {
    if (!inventarioId || !inventarios) return null;
    return inventarios.find((inv) => inv.id === inventarioId);
  }, [inventarioId, inventarios]);

  // Validar cantidad disponible
  const cantidadDisponible = inventarioSeleccionado?.cantidadDisponible || 0;
  const excedeCantidad = cantidad > cantidadDisponible;

  const handleClose = () => {
    reset();
    setSelectedProducto(null);
    setSelectedInventario(null);
    onClose();
  };

  const onFormSubmit = (data: CreateMovimientoRequest) => {
    if (excedeCantidad) {
      return;
    }
    onSubmit(data);
    handleClose();
  };

  const handleProductoChange = (productoId: number) => {
    setSelectedProducto(productoId);
    setValue('inventarioId', 0);
    setSelectedInventario(null);
  };

  const handleInventarioChange = (inventarioId: number) => {
    setSelectedInventario(inventarioId);
    setValue('inventarioId', inventarioId);
  };

  return (
    <Modal
      title="Salida de Inventario"
      open={open}
      onCancel={handleClose}
      onOk={handleSubmit(onFormSubmit)}
      okText="Registrar Salida"
      cancelText="Cancelar"
      width={700}
      confirmLoading={loading}
      okButtonProps={{ disabled: excedeCantidad }}
    >
      <Form layout="vertical" className="mt-4">
        {/* Selección de Producto */}
        <Row gutter={16}>
          <Col span={24}>
            <Form.Item label="Producto" required>
              <Select
                placeholder="Seleccione el producto"
                loading={loadingProductos}
                showSearch
                value={selectedProducto}
                onChange={handleProductoChange}
                filterOption={(input, option) =>
                  (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
                }
                options={productos?.map((producto) => ({
                  value: producto.id,
                  label: `${producto.codigo} - ${producto.descripcion}`,
                }))}
              />
            </Form.Item>
          </Col>
        </Row>

        {/* Selección de Registro de Inventario */}
        {selectedProducto && (
          <Row gutter={16}>
            <Col span={24}>
              <Form.Item label="Registro de Inventario" required help="Seleccione el lote/ubicación del cual desea retirar">
                <Controller
                  name="inventarioId"
                  control={control}
                  rules={{ required: true, min: 1 }}
                  render={({ field }) => (
                    <Select
                      {...field}
                      placeholder="Seleccione el registro de inventario"
                      loading={loadingInventarios}
                      showSearch
                      onChange={handleInventarioChange}
                      filterOption={(input, option) =>
                        (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
                      }
                      options={inventarios?.map((inv) => ({
                        value: inv.id,
                        label: `${inv.ubicacion} - Lote: ${inv.lote || 'N/A'} - Disponible: ${inv.cantidadDisponible}`,
                      }))}
                    />
                  )}
                />
              </Form.Item>
            </Col>
          </Row>
        )}

        {/* Información del inventario seleccionado */}
        {inventarioSeleccionado && (
          <Alert
            message="Información del Inventario"
            description={
              <div className="space-y-1 text-sm">
                <div><strong>Ubicación:</strong> {inventarioSeleccionado.ubicacion}</div>
                <div><strong>Lote:</strong> {inventarioSeleccionado.lote || 'N/A'}</div>
                <div><strong>Cantidad Disponible:</strong> {inventarioSeleccionado.cantidadDisponible}</div>
                <div><strong>Cantidad Reservada:</strong> {inventarioSeleccionado.cantidadReservada}</div>
                <div><strong>Fecha de Ingreso:</strong> {new Date(inventarioSeleccionado.fechaIngreso).toLocaleDateString('es-PY')}</div>
                {inventarioSeleccionado.fechaVencimiento && (
                  <div><strong>Vencimiento:</strong> {new Date(inventarioSeleccionado.fechaVencimiento).toLocaleDateString('es-PY')}</div>
                )}
              </div>
            }
            type="info"
            className="mb-4"
          />
        )}

        {/* Tipo de Movimiento y Cantidad */}
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item label="Tipo de Movimiento" required>
              <Controller
                name="tipoMovimiento"
                control={control}
                render={({ field }) => (
                  <Select
                    {...field}
                    options={TIPOS_MOVIMIENTO.map((tipo) => ({
                      value: tipo.value,
                      label: tipo.label,
                    }))}
                  />
                )}
              />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item
              label="Cantidad"
              required
              validateStatus={excedeCantidad ? 'error' : ''}
              help={excedeCantidad ? `Excede la cantidad disponible (${cantidadDisponible})` : ''}
            >
              <Controller
                name="cantidad"
                control={control}
                rules={{ required: true, min: 1, max: cantidadDisponible }}
                render={({ field }) => (
                  <InputNumber
                    {...field}
                    min={1}
                    max={cantidadDisponible}
                    placeholder="Cantidad a retirar"
                    className="w-full"
                    status={excedeCantidad ? 'error' : ''}
                  />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        {/* Pedido Relacionado (opcional) */}
        {tipoMovimiento === 'RESERVA' && (
          <Row gutter={16}>
            <Col span={24}>
              <Form.Item label="Pedido Relacionado" help="Seleccione el pedido para el cual se reserva el inventario">
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
        )}

        {/* Referencia */}
        <Row gutter={16}>
          <Col span={24}>
            <Form.Item label="Referencia" help="Número de guía, documento, etc.">
              <Controller
                name="referencia"
                control={control}
                render={({ field }) => (
                  <Input {...field} placeholder="Ej: GUIA-2024-001" maxLength={100} />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        {/* Observaciones */}
        <Row gutter={16}>
          <Col span={24}>
            <Form.Item label="Observaciones">
              <Controller
                name="observaciones"
                control={control}
                render={({ field }) => (
                  <Input.TextArea
                    {...field}
                    rows={3}
                    placeholder="Motivo de la salida, destino, etc..."
                    maxLength={500}
                    showCount
                  />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        {/* Descripción del tipo de movimiento */}
        {tipoMovimiento && (
          <Alert
            message={TIPOS_MOVIMIENTO.find((t) => t.value === tipoMovimiento)?.description}
            type="info"
            showIcon
          />
        )}
      </Form>
    </Modal>
  );
};
