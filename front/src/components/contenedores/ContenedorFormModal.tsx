import { Modal, Form, Input, Select, InputNumber, DatePicker, Row, Col } from 'antd';
import { useEffect } from 'react';
import { Contenedor } from '@/types/contenedor.types';
import dayjs from 'dayjs';

const { TextArea } = Input;

interface ContenedorFormModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: any) => void;
  initialData?: Contenedor;
  loading?: boolean;
}

export const ContenedorFormModal = ({
  open,
  onClose,
  onSubmit,
  initialData,
  loading = false,
}: ContenedorFormModalProps) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (open && initialData) {
      const formData = {
        ...initialData,
        fechaLlegada: initialData.fechaLlegada ? dayjs(initialData.fechaLlegada) : undefined,
        fechaSalida: initialData.fechaSalida ? dayjs(initialData.fechaSalida) : undefined,
      };
      form.setFieldsValue(formData);
    } else if (open) {
      form.resetFields();
    }
  }, [open, initialData, form]);

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      // Convert dates to ISO strings
      if (values.fechaLlegada) {
        values.fechaLlegada = values.fechaLlegada.toISOString();
      }
      if (values.fechaSalida) {
        values.fechaSalida = values.fechaSalida.toISOString();
      }
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
      title={initialData ? 'Editar Container' : 'Nuevo Container'}
      open={open}
      onOk={handleOk}
      onCancel={handleCancel}
      width={800}
      confirmLoading={loading}
      okText={initialData ? 'Actualizar' : 'Crear'}
      cancelText="Cancelar"
    >
      <Form form={form} layout="vertical" className="mt-4">
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              label="Número de Container"
              name="numeroContenedor"
              rules={[{ required: true, message: 'Ingrese el número' }]}
            >
              <Input placeholder="Ej: ABCD1234567" />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item
              label="Tipo"
              name="tipo"
              rules={[{ required: true, message: 'Seleccione el tipo' }]}
            >
              <Select placeholder="Seleccione tipo">
                <Select.Option value="20FT">20FT</Select.Option>
                <Select.Option value="40FT">40FT</Select.Option>
                <Select.Option value="40HC">40HC</Select.Option>
                <Select.Option value="45FT">45FT</Select.Option>
                <Select.Option value="REEFER20">REEFER 20</Select.Option>
                <Select.Option value="REEFER40">REEFER 40</Select.Option>
              </Select>
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              label="Estado"
              name="estado"
              rules={[{ required: true, message: 'Seleccione el estado' }]}
              initialValue="DISPONIBLE"
            >
              <Select placeholder="Estado">
                <Select.Option value="DISPONIBLE">Disponible</Select.Option>
                <Select.Option value="EN_TRANSITO">En Tránsito</Select.Option>
                <Select.Option value="EN_PUERTO">En Puerto</Select.Option>
                <Select.Option value="EN_DEPOSITO">En Depósito</Select.Option>
                <Select.Option value="EN_CONSOLIDACION">En Consolidación</Select.Option>
                <Select.Option value="CERRADO">Cerrado</Select.Option>
                <Select.Option value="DESPACHADO">Despachado</Select.Option>
              </Select>
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item
              label="Ubicación Actual"
              name="ubicacionActual"
            >
              <Input placeholder="Ciudad, País" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              label="Capacidad (kg)"
              name="capacidadKg"
              rules={[{ required: true, message: 'Ingrese la capacidad' }]}
            >
              <InputNumber min={0} className="w-full" placeholder="30000" />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item
              label="Capacidad (m³)"
              name="capacidadM3"
              rules={[{ required: true, message: 'Ingrese la capacidad' }]}
            >
              <InputNumber min={0} step={0.1} className="w-full" placeholder="67.0" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              label="Puerto Origen"
              name="puertoOrigen"
            >
              <Input placeholder="Ej: Santos, Brasil" />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item
              label="Puerto Destino"
              name="puertoDestino"
            >
              <Input placeholder="Ej: Asunción, Paraguay" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              label="Naviera"
              name="naviera"
            >
              <Input placeholder="Nombre de la naviera" />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item
              label="Número de Viaje"
              name="numeroViaje"
            >
              <Input placeholder="Número de viaje" />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              label="Fecha de Llegada"
              name="fechaLlegada"
            >
              <DatePicker className="w-full" format="DD/MM/YYYY" />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item
              label="Fecha de Salida"
              name="fechaSalida"
            >
              <DatePicker className="w-full" format="DD/MM/YYYY" />
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
