import { Modal, Form, Input, Select, InputNumber, Switch, Row, Col, DatePicker, AutoComplete } from 'antd';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { pedidoSchema, PedidoFormData } from '@/schemas/pedido.schema';
import { Pedido, TipoCarga } from '@/types/pedido.types';
import { getPaises, getCiudadesPorPais, getAllCiudades } from '@/utils/geo-data';
import { useState, useMemo, useEffect } from 'react';
import dayjs from 'dayjs';
import { useClientes } from '@/hooks/useClientes';

interface PedidoFormModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: PedidoFormData) => void;
  initialData?: Pedido;
  loading?: boolean;
}

const TIPOS_CARGA: { value: TipoCarga; label: string }[] = [
  { value: 'FCL', label: 'FCL - Full Container Load' },
  { value: 'LCL', label: 'LCL - Less than Container Load' },
  { value: 'GRANEL', label: 'Granel' },
  { value: 'PERECEDERO', label: 'Perecedero' },
  { value: 'PELIGROSO', label: 'Peligroso' },
  { value: 'FRAGIL', label: 'Frágil' },
];

const MONEDAS = [
  { value: 'PYG', label: 'PYG - Guaraníes' },
  { value: 'USD', label: 'USD - Dólares' },
  { value: 'EUR', label: 'EUR - Euros' },
  { value: 'BRL', label: 'BRL - Reales' },
  { value: 'ARS', label: 'ARS - Pesos Argentinos' },
];

export const PedidoFormModal = ({
  open,
  onClose,
  onSubmit,
  initialData,
  loading = false,
}: PedidoFormModalProps) => {
  const [paisOrigen, setPaisOrigen] = useState<string>(initialData?.paisOrigen || '');
  const [paisDestino, setPaisDestino] = useState<string>(initialData?.paisDestino || '');
  const [requiresSeguro, setRequiresSeguro] = useState(initialData?.requiereSeguro || false);

  // Obtener clientes para el select
  const { clientes, isLoading: loadingClientes } = useClientes({ page: 0, size: 100 });

  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
    watch,
  } = useForm<PedidoFormData>({
    resolver: zodResolver(pedidoSchema),
    defaultValues: initialData
      ? {
          ...initialData,
          fechaEstimadaLlegada: initialData.fechaEstimadaLlegada
            ? dayjs(initialData.fechaEstimadaLlegada).format('YYYY-MM-DD')
            : undefined,
        }
      : {
          clienteId: 0,
          paisOrigen: '',
          paisDestino: '',
          ciudadOrigen: '',
          ciudadDestino: '',
          tipoCarga: 'FCL',
          descripcionMercaderia: '',
          pesoTotalKg: 0,
          volumenTotalM3: 0,
          valorDeclarado: 0,
          moneda: 'PYG',
          requiereSeguro: false,
          valorSeguro: 0,
        },
  });

  // Watch para cambios en países
  const paisOrigenActual = watch('paisOrigen');
  const paisDestinoActual = watch('paisDestino');

  // Ciudades filtradas
  const ciudadesOrigen = useMemo(() => {
    if (paisOrigenActual) {
      return getCiudadesPorPais(paisOrigenActual);
    }
    return getAllCiudades();
  }, [paisOrigenActual]);

  const ciudadesDestino = useMemo(() => {
    if (paisDestinoActual) {
      return getCiudadesPorPais(paisDestinoActual);
    }
    return getAllCiudades();
  }, [paisDestinoActual]);

  // Actualizar el formulario cuando cambien los datos iniciales o se abra el modal
  useEffect(() => {
    if (open) {
      if (initialData) {
        // Modo edición: cargar datos del pedido
        const formData: PedidoFormData = {
          ...initialData,
          fechaEstimadaLlegada: initialData.fechaEstimadaLlegada
            ? dayjs(initialData.fechaEstimadaLlegada).format('YYYY-MM-DD')
            : undefined,
        };
        reset(formData);
        setPaisOrigen(initialData.paisOrigen || '');
        setPaisDestino(initialData.paisDestino || '');
        setRequiresSeguro(initialData.requiereSeguro || false);
      } else {
        // Modo creación: valores por defecto
        reset({
          clienteId: 0,
          paisOrigen: '',
          paisDestino: '',
          ciudadOrigen: '',
          ciudadDestino: '',
          tipoCarga: 'FCL',
          descripcionMercaderia: '',
          pesoTotalKg: 0,
          volumenTotalM3: 0,
          valorDeclarado: 0,
          moneda: 'PYG',
          requiereSeguro: false,
          valorSeguro: 0,
        });
        setPaisOrigen('');
        setPaisDestino('');
        setRequiresSeguro(false);
      }
    }
  }, [open, initialData, reset]);

  const handleClose = () => {
    reset();
    setPaisOrigen('');
    setPaisDestino('');
    setRequiresSeguro(false);
    onClose();
  };

  const onFormSubmit = (data: PedidoFormData) => {
    onSubmit(data);
    handleClose();
  };

  const paises = getPaises();

  return (
    <Modal
      title={initialData ? 'Editar Pedido' : 'Nuevo Pedido'}
      open={open}
      onCancel={handleClose}
      onOk={handleSubmit(onFormSubmit)}
      okText={initialData ? 'Actualizar' : 'Crear'}
      cancelText="Cancelar"
      width={1000}
      confirmLoading={loading}
    >
      <Form layout="vertical" className="mt-4">
        <Row gutter={16}>
          {/* Cliente */}
          <Col span={24}>
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
        </Row>

        <Row gutter={16}>
          {/* Tipo de Carga */}
          <Col span={12}>
            <Form.Item
              label="Tipo de Carga"
              validateStatus={errors.tipoCarga ? 'error' : ''}
              help={errors.tipoCarga?.message}
              required
            >
              <Controller
                name="tipoCarga"
                control={control}
                render={({ field }) => (
                  <Select {...field} placeholder="Seleccione tipo de carga" options={TIPOS_CARGA} />
                )}
              />
            </Form.Item>
          </Col>

          {/* Moneda */}
          <Col span={12}>
            <Form.Item
              label="Moneda"
              validateStatus={errors.moneda ? 'error' : ''}
              help={errors.moneda?.message}
              required
            >
              <Controller
                name="moneda"
                control={control}
                render={({ field }) => (
                  <Select {...field} placeholder="Seleccione moneda" options={MONEDAS} />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        {/* Origen y Destino */}
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              label="País de Origen"
              validateStatus={errors.paisOrigen ? 'error' : ''}
              help={errors.paisOrigen?.message}
              required
            >
              <Controller
                name="paisOrigen"
                control={control}
                render={({ field }) => (
                  <AutoComplete
                    {...field}
                    options={paises.map((p) => ({ value: p }))}
                    placeholder="Ej: China"
                    filterOption={(inputValue, option) =>
                      option!.value.toLowerCase().indexOf(inputValue.toLowerCase()) !== -1
                    }
                    onChange={(value) => {
                      field.onChange(value);
                      setPaisOrigen(value);
                    }}
                  />
                )}
              />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item
              label="País de Destino"
              validateStatus={errors.paisDestino ? 'error' : ''}
              help={errors.paisDestino?.message}
              required
            >
              <Controller
                name="paisDestino"
                control={control}
                render={({ field }) => (
                  <AutoComplete
                    {...field}
                    options={paises.map((p) => ({ value: p }))}
                    placeholder="Ej: Paraguay"
                    filterOption={(inputValue, option) =>
                      option!.value.toLowerCase().indexOf(inputValue.toLowerCase()) !== -1
                    }
                    onChange={(value) => {
                      field.onChange(value);
                      setPaisDestino(value);
                    }}
                  />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item label="Ciudad de Origen" validateStatus={errors.ciudadOrigen ? 'error' : ''} help={errors.ciudadOrigen?.message}>
              <Controller
                name="ciudadOrigen"
                control={control}
                render={({ field }) => (
                  <AutoComplete
                    {...field}
                    options={ciudadesOrigen.map((c) => ({ value: c }))}
                    placeholder="Ej: Shanghai"
                    filterOption={(inputValue, option) =>
                      option!.value.toLowerCase().indexOf(inputValue.toLowerCase()) !== -1
                    }
                  />
                )}
              />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item label="Ciudad de Destino" validateStatus={errors.ciudadDestino ? 'error' : ''} help={errors.ciudadDestino?.message}>
              <Controller
                name="ciudadDestino"
                control={control}
                render={({ field }) => (
                  <AutoComplete
                    {...field}
                    options={ciudadesDestino.map((c) => ({ value: c }))}
                    placeholder="Ej: Asunción"
                    filterOption={(inputValue, option) =>
                      option!.value.toLowerCase().indexOf(inputValue.toLowerCase()) !== -1
                    }
                  />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        {/* Descripción de Mercadería */}
        <Row gutter={16}>
          <Col span={24}>
            <Form.Item
              label="Descripción de Mercadería"
              validateStatus={errors.descripcionMercaderia ? 'error' : ''}
              help={errors.descripcionMercaderia?.message}
              required
            >
              <Controller
                name="descripcionMercaderia"
                control={control}
                render={({ field }) => (
                  <Input.TextArea {...field} rows={3} placeholder="Describa la mercadería a transportar..." maxLength={500} showCount />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        {/* Datos de Transporte */}
        <Row gutter={16}>
          <Col span={8}>
            <Form.Item
              label="Peso Total (kg)"
              validateStatus={errors.pesoTotalKg ? 'error' : ''}
              help={errors.pesoTotalKg?.message}
              required
            >
              <Controller
                name="pesoTotalKg"
                control={control}
                render={({ field }) => <InputNumber {...field} placeholder="0.00" min={0} max={999999} className="w-full" />}
              />
            </Form.Item>
          </Col>

          <Col span={8}>
            <Form.Item
              label="Volumen Total (m³)"
              validateStatus={errors.volumenTotalM3 ? 'error' : ''}
              help={errors.volumenTotalM3?.message}
              required
            >
              <Controller
                name="volumenTotalM3"
                control={control}
                render={({ field }) => <InputNumber {...field} placeholder="0.00" min={0} max={999999} step={0.01} className="w-full" />}
              />
            </Form.Item>
          </Col>

          <Col span={8}>
            <Form.Item
              label="Valor Declarado"
              validateStatus={errors.valorDeclarado ? 'error' : ''}
              help={errors.valorDeclarado?.message}
              required
            >
              <Controller
                name="valorDeclarado"
                control={control}
                render={({ field }) => <InputNumber {...field} placeholder="0.00" min={0} className="w-full" formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')} />}
              />
            </Form.Item>
          </Col>
        </Row>

        {/* Información Adicional */}
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item label="Fecha Estimada de Llegada" validateStatus={errors.fechaEstimadaLlegada ? 'error' : ''} help={errors.fechaEstimadaLlegada?.message}>
              <Controller
                name="fechaEstimadaLlegada"
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

          <Col span={12}>
            <Form.Item label="N° BL/AWB" validateStatus={errors.numeroBlAwb ? 'error' : ''} help={errors.numeroBlAwb?.message}>
              <Controller
                name="numeroBlAwb"
                control={control}
                render={({ field }) => <Input {...field} placeholder="Número de conocimiento de embarque" maxLength={100} />}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item label="Puerto de Embarque" validateStatus={errors.puertoEmbarque ? 'error' : ''} help={errors.puertoEmbarque?.message}>
              <Controller
                name="puertoEmbarque"
                control={control}
                render={({ field }) => <Input {...field} placeholder="Puerto de origen" maxLength={200} />}
              />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item label="Puerto de Destino" validateStatus={errors.puertoDestino ? 'error' : ''} help={errors.puertoDestino?.message}>
              <Controller
                name="puertoDestino"
                control={control}
                render={({ field }) => <Input {...field} placeholder="Puerto de destino" maxLength={200} />}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item label="Empresa de Transporte" validateStatus={errors.empresaTransporte ? 'error' : ''} help={errors.empresaTransporte?.message}>
              <Controller
                name="empresaTransporte"
                control={control}
                render={({ field }) => <Input {...field} placeholder="Nombre de la empresa transportista" maxLength={200} />}
              />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item label="Dirección de Entrega" validateStatus={errors.direccionEntrega ? 'error' : ''} help={errors.direccionEntrega?.message}>
              <Controller
                name="direccionEntrega"
                control={control}
                render={({ field }) => <Input {...field} placeholder="Dirección final de entrega" maxLength={500} />}
              />
            </Form.Item>
          </Col>
        </Row>

        {/* Seguro */}
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item label="Requiere Seguro">
              <Controller
                name="requiereSeguro"
                control={control}
                render={({ field }) => (
                  <Switch
                    checked={field.value}
                    onChange={(checked) => {
                      field.onChange(checked);
                      setRequiresSeguro(checked);
                    }}
                    checkedChildren="Sí"
                    unCheckedChildren="No"
                  />
                )}
              />
            </Form.Item>
          </Col>

          <Col span={12} style={{ display: requiresSeguro ? 'block' : 'none' }}>
            <Form.Item label="Valor del Seguro" validateStatus={errors.valorSeguro ? 'error' : ''} help={errors.valorSeguro?.message}>
              <Controller
                name="valorSeguro"
                control={control}
                render={({ field }) => <InputNumber {...field} placeholder="0.00" min={0} className="w-full" formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')} />}
              />
            </Form.Item>
          </Col>
        </Row>

        {/* Observaciones */}
        <Row gutter={16}>
          <Col span={24}>
            <Form.Item label="Observaciones" validateStatus={errors.observaciones ? 'error' : ''} help={errors.observaciones?.message}>
              <Controller
                name="observaciones"
                control={control}
                render={({ field }) => <Input.TextArea {...field} rows={3} placeholder="Observaciones adicionales..." maxLength={1000} showCount />}
              />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
};
