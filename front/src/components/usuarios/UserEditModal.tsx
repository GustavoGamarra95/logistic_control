import { Modal, Form, Input, Select, Switch, Button, Space, Divider, message } from 'antd';
import { LockOutlined, UserOutlined, MailOutlined } from '@ant-design/icons';
import { Usuario } from '@/types/usuario.types';
import { UserRole } from '@/types/auth.types';
import { useState } from 'react';

interface UserEditModalProps {
  usuario: Usuario | null;
  open: boolean;
  onClose: () => void;
  onUpdate: (id: number, data: any) => void;
  onResetPassword: (id: number, newPassword: string) => void;
}

interface FormValues {
  username: string;
  email: string;
  nombre: string;
  apellido: string;
  telefono?: string;
  roles: UserRole[];
  activo: boolean;
}

interface PasswordResetFormValues {
  newPassword: string;
  confirmPassword: string;
}

export function UserEditModal({ usuario, open, onClose, onUpdate, onResetPassword }: UserEditModalProps) {
  const [form] = Form.useForm<FormValues>();
  const [passwordForm] = Form.useForm<PasswordResetFormValues>();
  const [showPasswordReset, setShowPasswordReset] = useState(false);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (usuario) {
        onUpdate(usuario.id, values);
        onClose();
      }
    } catch (error) {
      console.error('Validation failed:', error);
    }
  };

  const handleResetPassword = async () => {
    try {
      const values = await passwordForm.validateFields();
      if (values.newPassword !== values.confirmPassword) {
        message.error('Las contraseñas no coinciden');
        return;
      }
      if (usuario) {
        onResetPassword(usuario.id, values.newPassword);
        passwordForm.resetFields();
        setShowPasswordReset(false);
      }
    } catch (error) {
      console.error('Validation failed:', error);
    }
  };

  const rolesOptions: { label: string; value: UserRole }[] = [
    { label: 'ADMIN - Administrador del sistema', value: 'ADMIN' },
    { label: 'OPERADOR - Operaciones generales', value: 'OPERADOR' },
    { label: 'FINANZAS - Gestión financiera', value: 'FINANZAS' },
    { label: 'DEPOSITO - Gestión de almacén', value: 'DEPOSITO' },
    { label: 'CLIENTE - Cliente del sistema', value: 'CLIENTE' },
  ];

  return (
    <Modal
      title={`Editar Usuario: ${usuario?.username || ''}`}
      open={open}
      onCancel={onClose}
      width={700}
      footer={[
        <Button key="cancel" onClick={onClose}>
          Cancelar
        </Button>,
        <Button key="submit" type="primary" onClick={handleSubmit}>
          Guardar Cambios
        </Button>,
      ]}
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={usuario ? {
          username: usuario.username,
          email: usuario.email,
          nombre: usuario.nombre,
          apellido: usuario.apellido,
          telefono: usuario.telefono,
          roles: usuario.roles,
          activo: usuario.activo,
        } : {}}
      >
        <Form.Item
          label="Username"
          name="username"
          rules={[{ required: true, message: 'Username es requerido' }]}
        >
          <Input prefix={<UserOutlined />} disabled />
        </Form.Item>

        <Form.Item
          label="Email"
          name="email"
          rules={[
            { required: true, message: 'Email es requerido' },
            { type: 'email', message: 'Email inválido' },
          ]}
        >
          <Input prefix={<MailOutlined />} />
        </Form.Item>

        <Space style={{ width: '100%' }} size="middle">
          <Form.Item
            label="Nombre"
            name="nombre"
            style={{ flex: 1, minWidth: '45%' }}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="Apellido"
            name="apellido"
            style={{ flex: 1, minWidth: '45%' }}
          >
            <Input />
          </Form.Item>
        </Space>

        <Form.Item label="Teléfono" name="telefono">
          <Input placeholder="+595 XXX XXX XXX" />
        </Form.Item>

        <Form.Item
          label="Roles"
          name="roles"
          rules={[{ required: true, message: 'Debe seleccionar al menos un rol' }]}
        >
          <Select
            mode="multiple"
            placeholder="Seleccione uno o más roles"
            options={rolesOptions}
          />
        </Form.Item>

        <Form.Item name="activo" valuePropName="checked">
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Switch />
            <span>Usuario activo</span>
          </div>
        </Form.Item>
      </Form>

      <Divider />

      {!showPasswordReset ? (
        <Button
          type="dashed"
          icon={<LockOutlined />}
          onClick={() => setShowPasswordReset(true)}
          block
        >
          Restablecer Contraseña
        </Button>
      ) : (
        <>
          <h4 style={{ marginBottom: 16 }}>Restablecer Contraseña</h4>
          <Form form={passwordForm} layout="vertical">
            <Form.Item
              label="Nueva Contraseña"
              name="newPassword"
              rules={[
                { required: true, message: 'Contraseña requerida' },
                { min: 8, message: 'Mínimo 8 caracteres' },
              ]}
            >
              <Input.Password prefix={<LockOutlined />} />
            </Form.Item>

            <Form.Item
              label="Confirmar Contraseña"
              name="confirmPassword"
              dependencies={['newPassword']}
              rules={[
                { required: true, message: 'Confirme la contraseña' },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (!value || getFieldValue('newPassword') === value) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error('Las contraseñas no coinciden'));
                  },
                }),
              ]}
            >
              <Input.Password prefix={<LockOutlined />} />
            </Form.Item>

            <Space>
              <Button type="primary" onClick={handleResetPassword}>
                Cambiar Contraseña
              </Button>
              <Button onClick={() => {
                setShowPasswordReset(false);
                passwordForm.resetFields();
              }}>
                Cancelar
              </Button>
            </Space>
          </Form>
        </>
      )}
    </Modal>
  );
}
