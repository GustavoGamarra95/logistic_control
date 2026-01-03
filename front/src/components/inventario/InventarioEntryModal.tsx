import { Modal, Form, Input, Select, InputNumber, Row, Col, DatePicker } from 'antd';
import { useForm, Controller } from 'react-hook-form';
import { CreateInventarioRequest, EstadoInventario } from '@/types/inventario.types';
import { useProductos } from '@/hooks/useProductos';
import { useState } from 'react';
import dayjs from 'dayjs';

interface InventarioEntryModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: CreateInventarioRequest) => void;
  loading?: boolean;
}

const ESTADOS_INVENTARIO: { value: EstadoInventario; label: string }[] = [
  { value: 'DISPONIBLE', label: 'Disponible' },
  { value: 'RESERVADO', label: 'Reservado' },
  { value: 'EN_CUARENTENA', label: 'En Cuarentena' },
  { value: 'DAÑADO', label: 'Dañado' },
  { value: 'VENCIDO', label: 'Vencido' },
];

// Estructura de ubicaciones de almacén
const DEPOSITOS = ['PRINCIPAL', 'SECUNDARIO', 'TEMPORAL', 'REFRIGERADO'];
const ZONAS = ['A', 'B', 'C', 'D'];
const PASILLOS = Array.from({ length: 20 }, (_, i) => `P${(i + 1).toString().padStart(2, '0')}`);
const RACKS = Array.from({ length: 50 }, (_, i) => `R${(i + 1).toString().padStart(2, '0')}`);
const NIVELES = Array.from({ length: 10 }, (_, i) => `N${i + 1}`);

export const InventarioEntryModal = ({
  open,
  onClose,
  onSubmit,
  loading = false,
}: InventarioEntryModalProps) => {
  const [ubicacion, setUbicacion] = useState({
    deposito: '',
    zona: '',
    pasillo: '',
    rack: '',
    nivel: '',
  });

  // Obtener productos
  const { productos, isLoading: loadingProductos } = useProductos({ page: 0, size: 100 });

  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
    watch,
  } = useForm<CreateInventarioRequest>({
    defaultValues: {
      productoId: 0,
      almacenId: 1, // Almacén principal por defecto
      ubicacion: '',
      lote: '',
      fechaIngreso: dayjs().format('YYYY-MM-DD'),
      cantidadDisponible: 0,
      estado: 'DISPONIBLE',
      costoDiarioAlmacenaje: 0,
    },
  });

  const productoId = watch('productoId');

  // Generar string de ubicación
  const generarUbicacion = () => {
    const parts = [];
    if (ubicacion.deposito) parts.push(ubicacion.deposito);
    if (ubicacion.zona) parts.push(ubicacion.zona);
    if (ubicacion.pasillo) parts.push(ubicacion.pasillo);
    if (ubicacion.rack) parts.push(ubicacion.rack);
    if (ubicacion.nivel) parts.push(ubicacion.nivel);
    return parts.join('-');
  };

  const handleClose = () => {
    reset();
    setUbicacion({ deposito: '', zona: '', pasillo: '', rack: '', nivel: '' });
    onClose();
  };

  const onFormSubmit = (data: CreateInventarioRequest) => {
    // Generar ubicación completa
    const ubicacionCompleta = generarUbicacion();
    onSubmit({
      ...data,
      ubicacion: ubicacionCompleta || data.ubicacion,
    });
    handleClose();
  };

  return (
    <Modal
      title="Entrada de Inventario"
      open={open}
      onCancel={handleClose}
      onOk={handleSubmit(onFormSubmit)}
      okText="Registrar Entrada"
      cancelText="Cancelar"
      width={800}
      confirmLoading={loading}
    >
      <Form layout="vertical" className="mt-4">
        <Row gutter={16}>
          <Col span={24}>
            <Form.Item label="Producto" required>
              <Controller
                name="productoId"
                control={control}
                rules={{ required: true, min: 1 }}
                render={({ field }) => (
                  <Select
                    {...field}
                    placeholder="Seleccione el producto"
                    loading={loadingProductos}
                    showSearch
                    filterOption={(input, option) =>
                      (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
                    }
                    options={productos?.map((producto) => ({
                      value: producto.id,
                      label: `${producto.codigo} - ${producto.descripcion}`,
                    }))}
                  />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item label="Cantidad a Ingresar" required>
              <Controller
                name="cantidadDisponible"
                control={control}
                rules={{ required: true, min: 1 }}
                render={({ field }) => (
                  <InputNumber
                    {...field}
                    min={1}
                    placeholder="Cantidad"
                    className="w-full"
                  />
                )}
              />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item label="Estado Inicial" required>
              <Controller
                name="estado"
                control={control}
                render={({ field }) => (
                  <Select {...field} options={ESTADOS_INVENTARIO} />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item label="Lote/Batch">
              <Controller
                name="lote"
                control={control}
                render={({ field }) => (
                  <Input {...field} placeholder="Ej: LOTE-2024-001" maxLength={50} />
                )}
              />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item label="Fecha de Ingreso" required>
              <Controller
                name="fechaIngreso"
                control={control}
                render={({ field }) => (
                  <DatePicker
                    {...field}
                    value={field.value ? dayjs(field.value) : dayjs()}
                    onChange={(date) => field.onChange(date ? date.format('YYYY-MM-DD') : dayjs().format('YYYY-MM-DD'))}
                    format="DD/MM/YYYY"
                    className="w-full"
                  />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item label="Fecha de Vencimiento">
              <Controller
                name="fechaVencimiento"
                control={control}
                render={({ field }) => (
                  <DatePicker
                    {...field}
                    value={field.value ? dayjs(field.value) : null}
                    onChange={(date) => field.onChange(date ? date.format('YYYY-MM-DD') : null)}
                    format="DD/MM/YYYY"
                    placeholder="Opcional"
                    className="w-full"
                  />
                )}
              />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item label="Costo Diario Almacenaje">
              <Controller
                name="costoDiarioAlmacenaje"
                control={control}
                render={({ field }) => (
                  <InputNumber
                    {...field}
                    min={0}
                    placeholder="0.00"
                    className="w-full"
                    formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                    parser={(value) => value!.replace(/,/g, '')}
                  />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        {/* Ubicación en Almacén */}
        <div className="mb-4">
          <div className="text-sm font-medium mb-2">Ubicación en Almacén</div>
          <Row gutter={8}>
            <Col span={5}>
              <Select
                placeholder="Depósito"
                value={ubicacion.deposito}
                onChange={(value) => setUbicacion({ ...ubicacion, deposito: value })}
                options={DEPOSITOS.map((d) => ({ value: d, label: d }))}
                className="w-full"
                size="small"
              />
            </Col>
            <Col span={4}>
              <Select
                placeholder="Zona"
                value={ubicacion.zona}
                onChange={(value) => setUbicacion({ ...ubicacion, zona: value })}
                options={ZONAS.map((z) => ({ value: z, label: z }))}
                className="w-full"
                size="small"
              />
            </Col>
            <Col span={5}>
              <Select
                placeholder="Pasillo"
                value={ubicacion.pasillo}
                onChange={(value) => setUbicacion({ ...ubicacion, pasillo: value })}
                options={PASILLOS.map((p) => ({ value: p, label: p }))}
                className="w-full"
                size="small"
                showSearch
              />
            </Col>
            <Col span={5}>
              <Select
                placeholder="Rack"
                value={ubicacion.rack}
                onChange={(value) => setUbicacion({ ...ubicacion, rack: value })}
                options={RACKS.map((r) => ({ value: r, label: r }))}
                className="w-full"
                size="small"
                showSearch
              />
            </Col>
            <Col span={5}>
              <Select
                placeholder="Nivel"
                value={ubicacion.nivel}
                onChange={(value) => setUbicacion({ ...ubicacion, nivel: value })}
                options={NIVELES.map((n) => ({ value: n, label: n }))}
                className="w-full"
                size="small"
              />
            </Col>
          </Row>
          {generarUbicacion() && (
            <div className="text-xs text-gray-500 mt-1">
              Ubicación: <span className="font-mono font-semibold">{generarUbicacion()}</span>
            </div>
          )}
        </div>

        {/* Ubicación Manual (alternativa) */}
        <Row gutter={16}>
          <Col span={24}>
            <Form.Item label="Ubicación Manual (opcional)" help="Si no selecciona ubicación arriba, ingrese manualmente">
              <Controller
                name="ubicacion"
                control={control}
                render={({ field }) => (
                  <Input {...field} placeholder="Ej: DEP-A-P01-R15-N3" maxLength={100} />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

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
                    placeholder="Observaciones sobre el ingreso..."
                    maxLength={500}
                    showCount
                  />
                )}
              />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
};
