import { useEffect } from 'react';
import { Modal, Form, Input, Select, Rate, InputNumber } from 'antd';
import { Proveedor } from '@/types/proveedor.types';

interface ProveedorFormModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: any) => void;
  initialData?: Proveedor;
  loading?: boolean;
}

const { TextArea } = Input;

export const ProveedorFormModal = ({
  open,
  onClose,
  onSubmit,
  initialData,
  loading = false,
}: ProveedorFormModalProps) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (open) {
      if (initialData) {
        form.setFieldsValue({
          ruc: initialData.ruc,
          nombre: initialData.nombre,
          razonSocial: initialData.razonSocial,
          tipo: initialData.tipo,
          email: initialData.email,
          telefono: initialData.telefono,
          direccion: initialData.direccion,
          ciudad: initialData.ciudad,
          pais: initialData.pais,
          contactoPrincipal: initialData.contactoPrincipal,
          calificacion: initialData.calificacion,
          condicionesPago: initialData.condicionesPago,
          notas: initialData.notas,
        });
      } else {
        form.resetFields();
      }
    }
  }, [open, initialData, form]);

  const handleOk = () => {
    form.validateFields().then((values) => {
      onSubmit(values);
      form.resetFields();
    });
  };

  const handleCancel = () => {
    form.resetFields();
    onClose();
  };

  return (
    <Modal
      title={initialData ? 'Editar Proveedor' : 'Nuevo Proveedor'}
      open={open}
      onOk={handleOk}
      onCancel={handleCancel}
      confirmLoading={loading}
      width={700}
      okText="Guardar"
      cancelText="Cancelar"
    >
      <Form
        form={form}
        layout="vertical"
        autoComplete="off"
      >
        <Form.Item
          name="razonSocial"
          label="Razón Social"
          rules={[{ required: true, message: 'Ingrese la razón social' }]}
        >
          <Input placeholder="Razón Social del proveedor" />
        </Form.Item>

        <Form.Item
          name="nombre"
          label="Nombre"
          rules={[{ required: true, message: 'Ingrese el nombre' }]}
        >
          <Input placeholder="Nombre comercial" />
        </Form.Item>

        <div className="grid grid-cols-2 gap-4">
          <Form.Item
            name="ruc"
            label="RUC"
            rules={[{ required: true, message: 'Ingrese el RUC' }]}
          >
            <Input placeholder="12345678-9" />
          </Form.Item>

          <Form.Item
            name="tipo"
            label="Tipo de Proveedor"
            rules={[{ required: true, message: 'Seleccione el tipo' }]}
          >
            <Select placeholder="Seleccione el tipo">
              <Select.Option value="TRANSPORTISTA">Transportista</Select.Option>
              <Select.Option value="NAVIERA">Naviera</Select.Option>
              <Select.Option value="AGENTE_ADUANAL">Agente Aduanal</Select.Option>
              <Select.Option value="ALMACEN">Almacén</Select.Option>
              <Select.Option value="SEGURO">Seguro</Select.Option>
              <Select.Option value="OTROS">Otros</Select.Option>
            </Select>
          </Form.Item>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <Form.Item
            name="email"
            label="Email"
            rules={[
              { required: true, message: 'Ingrese el email' },
              { type: 'email', message: 'Email inválido' }
            ]}
          >
            <Input placeholder="email@ejemplo.com" />
          </Form.Item>

          <Form.Item
            name="telefono"
            label="Teléfono"
            rules={[{ required: true, message: 'Ingrese el teléfono' }]}
          >
            <Input placeholder="+595 21 123456" />
          </Form.Item>
        </div>

        <Form.Item
          name="direccion"
          label="Dirección"
        >
          <Input placeholder="Dirección completa" />
        </Form.Item>

        <div className="grid grid-cols-2 gap-4">
          <Form.Item
            name="ciudad"
            label="Ciudad"
          >
            <Input placeholder="Asunción" />
          </Form.Item>

          <Form.Item
            name="pais"
            label="País"
            rules={[{ required: true, message: 'Ingrese el país' }]}
          >
            <Input placeholder="Paraguay" />
          </Form.Item>
        </div>

        <Form.Item
          name="contactoPrincipal"
          label="Contacto Principal"
        >
          <Input placeholder="Nombre del contacto" />
        </Form.Item>

        <div className="grid grid-cols-2 gap-4">
          <Form.Item
            name="calificacion"
            label="Calificación"
          >
            <Rate allowHalf />
          </Form.Item>

          <Form.Item
            name="condicionesPago"
            label="Condiciones de Pago"
          >
            <Input placeholder="Ej: 30 días" />
          </Form.Item>
        </div>

        <Form.Item
          name="notas"
          label="Notas"
        >
          <TextArea rows={3} placeholder="Notas adicionales" />
        </Form.Item>
      </Form>
    </Modal>
  );
};
