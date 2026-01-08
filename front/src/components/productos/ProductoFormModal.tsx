import { Modal, Form, Input, Select, InputNumber, Switch, Row, Col, Divider } from 'antd';
import { useEffect } from 'react';
import { Producto, TipoProducto } from '@/types/producto.types';

const { TextArea } = Input;

interface ProductoFormModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: any) => void;
  initialData?: Producto;
  loading?: boolean;
}

export const ProductoFormModal = ({
  open,
  onClose,
  onSubmit,
  initialData,
  loading = false,
}: ProductoFormModalProps) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (open && initialData) {
      form.setFieldsValue(initialData);
    } else if (open) {
      form.resetFields();
    }
  }, [open, initialData, form]);

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      onSubmit(values);
      form.resetFields();
      onClose();
    } catch (error) {
      console.error('Validation failed:', error);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    onClose();
  };

  return (
    <Modal
      title={initialData ? 'Editar Producto' : 'Nuevo Producto'}
      open={open}
      onOk={handleOk}
      onCancel={handleCancel}
      width={900}
      confirmLoading={loading}
      okText={initialData ? 'Actualizar' : 'Crear'}
      cancelText="Cancelar"
    >
      <Form form={form} layout="vertical" className="mt-4">
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              label="Código"
              name="codigo"
              rules={[{ required: true, message: 'Por favor ingrese el código' }]}
            >
              <Input placeholder="Ej: PROD-001" />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item
              label="NCM/Arancel"
              name="ncmArancel"
            >
              <Input placeholder="Código NCM" />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item
          label="Descripción"
          name="descripcion"
          rules={[{ required: true, message: 'Por favor ingrese la descripción' }]}
        >
          <Input placeholder="Descripción del producto" />
        </Form.Item>

        <Row gutter={16}>
          <Col span={8}>
            <Form.Item
              label="Tipo"
              name="tipo"
              rules={[{ required: true, message: 'Seleccione el tipo' }]}
              initialValue="GENERAL"
            >
              <Select placeholder="Seleccione tipo">
                <Select.Option value="GENERAL">General</Select.Option>
                <Select.Option value="PELIGROSO">Peligroso</Select.Option>
                <Select.Option value="PERECEDERO">Perecedero</Select.Option>
                <Select.Option value="FRAGIL">Frágil</Select.Option>
                <Select.Option value="REFRIGERADO">Refrigerado</Select.Option>
              </Select>
            </Form.Item>
          </Col>

          <Col span={8}>
            <Form.Item
              label="Categoría"
              name="categoria"
            >
              <Input placeholder="Categoría" />
            </Form.Item>
          </Col>

          <Col span={8}>
            <Form.Item
              label="Unidad de Medida"
              name="unidadMedida"
              rules={[{ required: true, message: 'Ingrese unidad' }]}
              initialValue="UNIDAD"
            >
              <Select placeholder="Unidad">
                <Select.Option value="UNIDAD">Unidad</Select.Option>
                <Select.Option value="KG">Kilogramo</Select.Option>
                <Select.Option value="TON">Tonelada</Select.Option>
                <Select.Option value="LT">Litro</Select.Option>
                <Select.Option value="M3">Metro cúbico</Select.Option>
              </Select>
            </Form.Item>
          </Col>
        </Row>

        <Divider>Dimensiones y Peso</Divider>

        <Row gutter={16}>
          <Col span={8}>
            <Form.Item
              label="Peso Unitario (kg)"
              name="pesoUnitarioKg"
              rules={[{ required: true, message: 'Ingrese el peso' }]}
            >
              <InputNumber
                min={0}
                step={0.01}
                className="w-full"
                placeholder="0.00"
                formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                parser={(value) => value!.replace(/,/g, '')}
              />
            </Form.Item>
          </Col>

          <Col span={8}>
            <Form.Item
              label="Volumen (m³)"
              name="volumenUnitarioM3"
              rules={[{ required: true, message: 'Ingrese el volumen' }]}
            >
              <InputNumber
                min={0}
                step={0.001}
                className="w-full"
                placeholder="0.000"
                formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                parser={(value) => value!.replace(/,/g, '')}
              />
            </Form.Item>
          </Col>

          <Col span={8}>
            <Form.Item
              label="Alto (cm)"
              name="alto"
            >
              <InputNumber
                min={0}
                className="w-full"
                placeholder="0"
                formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                parser={(value) => value!.replace(/,/g, '')}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={8}>
            <Form.Item
              label="Ancho (cm)"
              name="ancho"
            >
              <InputNumber
                min={0}
                className="w-full"
                placeholder="0"
                formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                parser={(value) => value!.replace(/,/g, '')}
              />
            </Form.Item>
          </Col>

          <Col span={8}>
            <Form.Item
              label="Largo (cm)"
              name="largo"
            >
              <InputNumber
                min={0}
                className="w-full"
                placeholder="0"
                formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                parser={(value) => value!.replace(/,/g, '')}
              />
            </Form.Item>
          </Col>
        </Row>

        <Divider>Valor y Moneda</Divider>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              label="Valor Unitario (Costo)"
              name="valorUnitario"
              rules={[{ required: true, message: 'Ingrese el valor' }]}
            >
              <InputNumber
                min={0}
                step={1000}
                className="w-full"
                placeholder="0"
                formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                parser={(value) => value!.replace(/,/g, '')}
              />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item
              label="Precio de Venta"
              name="precioVenta"
              rules={[{ required: true, message: 'Ingrese el precio de venta' }]}
            >
              <InputNumber
                min={0}
                step={1000}
                className="w-full"
                placeholder="0"
                formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                parser={(value) => value!.replace(/,/g, '')}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              label="Moneda"
              name="moneda"
              rules={[{ required: true, message: 'Seleccione moneda' }]}
              initialValue="PYG"
            >
              <Select placeholder="Moneda">
                <Select.Option value="PYG">Guaraníes (₲)</Select.Option>
                <Select.Option value="USD">Dólares ($)</Select.Option>
                <Select.Option value="EUR">Euros (€)</Select.Option>
              </Select>
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item
              label="IVA"
              name="tasaIva"
              rules={[{ required: true, message: 'Seleccione el IVA' }]}
              initialValue={10}
            >
              <Select placeholder="Tasa de IVA">
                <Select.Option value={0}>Exento (0%)</Select.Option>
                <Select.Option value={5}>IVA 5%</Select.Option>
                <Select.Option value={10}>IVA 10%</Select.Option>
              </Select>
            </Form.Item>
          </Col>
        </Row>

        <Divider>Características</Divider>

        <Row gutter={16}>
          <Col span={6}>
            <Form.Item
              label="Peligroso"
              name="esPeligroso"
              valuePropName="checked"
              initialValue={false}
            >
              <Switch />
            </Form.Item>
          </Col>

          <Col span={6}>
            <Form.Item
              label="Perecedero"
              name="esPerecedero"
              valuePropName="checked"
              initialValue={false}
            >
              <Switch />
            </Form.Item>
          </Col>

          <Col span={6}>
            <Form.Item
              label="Frágil"
              name="esFragil"
              valuePropName="checked"
              initialValue={false}
            >
              <Switch />
            </Form.Item>
          </Col>

          <Col span={6}>
            <Form.Item
              label="Refrigeración"
              name="requiereRefrigeracion"
              valuePropName="checked"
              initialValue={false}
            >
              <Switch />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item
          label="Observaciones"
          name="observaciones"
        >
          <TextArea rows={3} placeholder="Observaciones adicionales..." />
        </Form.Item>
      </Form>
    </Modal>
  );
};
