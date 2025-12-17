import { Modal, Form, Input, Select, InputNumber, Switch, Row, Col } from 'antd';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { clienteSchema, ClienteFormData } from '@/schemas/cliente.schema';
import { Cliente } from '@/types/cliente.types';
import { TIPOS_SERVICIO, PAISES_COMUNES } from '@/utils/constants';

interface ClienteFormModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: ClienteFormData) => void;
  initialData?: Cliente;
  loading?: boolean;
}

export const ClienteFormModal = ({
  open,
  onClose,
  onSubmit,
  initialData,
  loading = false,
}: ClienteFormModalProps) => {
  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<ClienteFormData>({
    resolver: zodResolver(clienteSchema),
    defaultValues: initialData || {
      razonSocial: '',
      nombreFantasia: '',
      ruc: '',
      dv: '',
      direccion: '',
      ciudad: '',
      pais: 'Paraguay',
      contacto: '',
      email: '',
      telefono: '',
      celular: '',
      tipoServicio: 'MARITIMO',
      creditoLimite: 0,
      esFacturadorElectronico: false,
      observaciones: '',
    },
  });

  const handleClose = () => {
    reset();
    onClose();
  };

  const onFormSubmit = (data: ClienteFormData) => {
    onSubmit(data);
    handleClose();
  };

  return (
    <Modal
      title={initialData ? 'Editar Cliente' : 'Nuevo Cliente'}
      open={open}
      onCancel={handleClose}
      onOk={handleSubmit(onFormSubmit)}
      okText={initialData ? 'Actualizar' : 'Crear'}
      cancelText="Cancelar"
      width={800}
      confirmLoading={loading}
    >
      <Form layout="vertical" className="mt-4">
        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              label="Razón Social"
              validateStatus={errors.razonSocial ? 'error' : ''}
              help={errors.razonSocial?.message}
              required
            >
              <Controller
                name="razonSocial"
                control={control}
                render={({ field }) => (
                  <Input {...field} placeholder="Empresa S.A." />
                )}
              />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item
              label="Nombre Fantasía"
              validateStatus={errors.nombreFantasia ? 'error' : ''}
              help={errors.nombreFantasia?.message}
            >
              <Controller
                name="nombreFantasia"
                control={control}
                render={({ field }) => (
                  <Input {...field} placeholder="Nombre comercial" />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={16}>
            <Form.Item
              label="RUC"
              validateStatus={errors.ruc ? 'error' : ''}
              help={errors.ruc?.message}
              required
            >
              <Controller
                name="ruc"
                control={control}
                render={({ field }) => (
                  <Input {...field} placeholder="12345678" maxLength={8} />
                )}
              />
            </Form.Item>
          </Col>

          <Col span={8}>
            <Form.Item
              label="DV"
              validateStatus={errors.dv ? 'error' : ''}
              help={errors.dv?.message}
              required
            >
              <Controller
                name="dv"
                control={control}
                render={({ field }) => (
                  <Input {...field} placeholder="9" maxLength={1} />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item
          label="Dirección"
          validateStatus={errors.direccion ? 'error' : ''}
          help={errors.direccion?.message}
          required
        >
          <Controller
            name="direccion"
            control={control}
            render={({ field }) => (
              <Input {...field} placeholder="Av. Principal 123" />
            )}
          />
        </Form.Item>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              label="Ciudad"
              validateStatus={errors.ciudad ? 'error' : ''}
              help={errors.ciudad?.message}
              required
            >
              <Controller
                name="ciudad"
                control={control}
                render={({ field }) => (
                  <Input {...field} placeholder="Asunción" />
                )}
              />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item
              label="País"
              validateStatus={errors.pais ? 'error' : ''}
              help={errors.pais?.message}
              required
            >
              <Controller
                name="pais"
                control={control}
                render={({ field }) => (
                  <Select {...field} placeholder="Seleccionar país">
                    {PAISES_COMUNES.map((pais) => (
                      <Select.Option key={pais} value={pais}>
                        {pais}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={8}>
            <Form.Item
              label="Contacto"
              validateStatus={errors.contacto ? 'error' : ''}
              help={errors.contacto?.message}
            >
              <Controller
                name="contacto"
                control={control}
                render={({ field }) => (
                  <Input {...field} placeholder="Juan Pérez" />
                )}
              />
            </Form.Item>
          </Col>

          <Col span={8}>
            <Form.Item
              label="Email"
              validateStatus={errors.email ? 'error' : ''}
              help={errors.email?.message}
              required
            >
              <Controller
                name="email"
                control={control}
                render={({ field }) => (
                  <Input {...field} type="email" placeholder="contacto@empresa.com" />
                )}
              />
            </Form.Item>
          </Col>

          <Col span={8}>
            <Form.Item
              label="Celular"
              validateStatus={errors.celular ? 'error' : ''}
              help={errors.celular?.message}
            >
              <Controller
                name="celular"
                control={control}
                render={({ field }) => (
                  <Input {...field} placeholder="+595 XXX XXX XXX" />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              label="Tipo de Servicio"
              validateStatus={errors.tipoServicio ? 'error' : ''}
              help={errors.tipoServicio?.message}
              required
            >
              <Controller
                name="tipoServicio"
                control={control}
                render={({ field }) => (
                  <Select {...field} placeholder="Seleccionar tipo">
                    {TIPOS_SERVICIO.map((tipo) => (
                      <Select.Option key={tipo.value} value={tipo.value}>
                        {tipo.label}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              />
            </Form.Item>
          </Col>

          <Col span={12}>
            <Form.Item
              label="Límite de Crédito (₲)"
              validateStatus={errors.creditoLimite ? 'error' : ''}
              help={errors.creditoLimite?.message}
              required
            >
              <Controller
                name="creditoLimite"
                control={control}
                render={({ field }) => (
                  <InputNumber
                    {...field}
                    className="w-full"
                    min={0}
                    formatter={(value) =>
                      `₲ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, '.')
                    }
                    parser={(value) => value!.replace(/₲\s?|(\.*)/g, '')}
                  />
                )}
              />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item
          label="Facturador Electrónico"
          validateStatus={errors.esFacturadorElectronico ? 'error' : ''}
          help={errors.esFacturadorElectronico?.message}
        >
          <Controller
            name="esFacturadorElectronico"
            control={control}
            render={({ field }) => (
              <Switch
                checked={field.value}
                onChange={field.onChange}
                checkedChildren="Sí"
                unCheckedChildren="No"
              />
            )}
          />
        </Form.Item>

        <Form.Item
          label="Observaciones"
          validateStatus={errors.observaciones ? 'error' : ''}
          help={errors.observaciones?.message}
        >
          <Controller
            name="observaciones"
            control={control}
            render={({ field }) => (
              <Input.TextArea
                {...field}
                rows={3}
                placeholder="Información adicional..."
              />
            )}
          />
        </Form.Item>
      </Form>
    </Modal>
  );
};
