import { useState } from 'react';
import {
  Card,
  Form,
  Input,
  Button,
  Avatar,
  Upload,
  message,
  Descriptions,
  Tabs,
  Space,
  Typography,
} from 'antd';
import {
  UserOutlined,
  MailOutlined,
  PhoneOutlined,
  LockOutlined,
  UploadOutlined,
} from '@ant-design/icons';
import { useAuthStore } from '@/store/authStore';
import { useTranslation } from 'react-i18next';
import type { UploadProps } from 'antd';

const { Title } = Typography;
const { TabPane } = Tabs;

interface ProfileFormValues {
  nombre: string;
  email: string;
  telefono?: string;
}

interface PasswordFormValues {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export default function PerfilPage() {
  const { t } = useTranslation();
  const user = useAuthStore((state) => state.user);
  const [loading, setLoading] = useState(false);
  const [profileForm] = Form.useForm<ProfileFormValues>();
  const [passwordForm] = Form.useForm<PasswordFormValues>();

  const uploadProps: UploadProps = {
    name: 'avatar',
    accept: 'image/*',
    maxCount: 1,
    beforeUpload: (file) => {
      const isImage = file.type.startsWith('image/');
      if (!isImage) {
        message.error('Solo puedes subir archivos de imagen');
      }
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        message.error('La imagen debe ser menor a 2MB');
      }
      return isImage && isLt2M;
    },
    onChange: (info) => {
      if (info.file.status === 'done') {
        message.success('Avatar actualizado exitosamente');
      } else if (info.file.status === 'error') {
        message.error('Error al subir el avatar');
      }
    },
  };

  const handleProfileUpdate = async (values: ProfileFormValues) => {
    setLoading(true);
    try {
      // TODO: Implement API call to update profile
      console.log('Updating profile:', values);
      message.success('Perfil actualizado exitosamente');
    } catch (error) {
      message.error('Error al actualizar el perfil');
    } finally {
      setLoading(false);
    }
  };

  const handlePasswordChange = async (values: PasswordFormValues) => {
    if (values.newPassword !== values.confirmPassword) {
      message.error('Las contraseñas no coinciden');
      return;
    }

    setLoading(true);
    try {
      // TODO: Implement API call to change password
      console.log('Changing password');
      message.success('Contraseña actualizada exitosamente');
      passwordForm.resetFields();
    } catch (error) {
      message.error('Error al cambiar la contraseña');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>Mi Perfil</Title>

      <Card>
        <Tabs defaultActiveKey="profile">
          <TabPane tab="Información Personal" key="profile">
            <Space vertical size="large" style={{ width: '100%' }}>
              <div style={{ textAlign: 'center', marginBottom: 24 }}>
                <Avatar
                  size={120}
                  icon={<UserOutlined />}
                  style={{ backgroundColor: '#0066CC', marginBottom: 16 }}
                />
                <div>
                  <Upload {...uploadProps} showUploadList={false}>
                    <Button icon={<UploadOutlined />}>Cambiar Avatar</Button>
                  </Upload>
                </div>
              </div>

              <Descriptions bordered column={1}>
                <Descriptions.Item label="Usuario">
                  {user?.username || 'N/A'}
                </Descriptions.Item>
                <Descriptions.Item label="Roles">
                  {user?.roles?.join(', ') || 'N/A'}
                </Descriptions.Item>
                <Descriptions.Item label="Email">
                  {user?.email || 'N/A'}
                </Descriptions.Item>
              </Descriptions>

              <Form
                form={profileForm}
                layout="vertical"
                onFinish={handleProfileUpdate}
                initialValues={{
                  nombre: user?.username,
                  email: user?.email,
                }}
              >
                <Form.Item
                  label="Nombre"
                  name="nombre"
                  rules={[{ required: true, message: 'Por favor ingrese su nombre' }]}
                >
                  <Input prefix={<UserOutlined />} placeholder="Nombre completo" />
                </Form.Item>

                <Form.Item
                  label="Email"
                  name="email"
                  rules={[
                    { required: true, message: 'Por favor ingrese su email' },
                    { type: 'email', message: 'Email inválido' },
                  ]}
                >
                  <Input prefix={<MailOutlined />} placeholder="correo@ejemplo.com" />
                </Form.Item>

                <Form.Item label="Teléfono" name="telefono">
                  <Input prefix={<PhoneOutlined />} placeholder="+595 XXX XXX XXX" />
                </Form.Item>

                <Form.Item>
                  <Button type="primary" htmlType="submit" loading={loading}>
                    Actualizar Perfil
                  </Button>
                </Form.Item>
              </Form>
            </Space>
          </TabPane>

          <TabPane tab="Seguridad" key="security">
            <Form
              form={passwordForm}
              layout="vertical"
              onFinish={handlePasswordChange}
            >
              <Form.Item
                label="Contraseña Actual"
                name="currentPassword"
                rules={[{ required: true, message: 'Por favor ingrese su contraseña actual' }]}
              >
                <Input.Password
                  prefix={<LockOutlined />}
                  placeholder="Contraseña actual"
                />
              </Form.Item>

              <Form.Item
                label="Nueva Contraseña"
                name="newPassword"
                rules={[
                  { required: true, message: 'Por favor ingrese una nueva contraseña' },
                  { min: 8, message: 'La contraseña debe tener al menos 8 caracteres' },
                ]}
              >
                <Input.Password
                  prefix={<LockOutlined />}
                  placeholder="Nueva contraseña"
                />
              </Form.Item>

              <Form.Item
                label="Confirmar Nueva Contraseña"
                name="confirmPassword"
                dependencies={['newPassword']}
                rules={[
                  { required: true, message: 'Por favor confirme su nueva contraseña' },
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
                <Input.Password
                  prefix={<LockOutlined />}
                  placeholder="Confirmar nueva contraseña"
                />
              </Form.Item>

              <Form.Item>
                <Button type="primary" htmlType="submit" loading={loading}>
                  Cambiar Contraseña
                </Button>
              </Form.Item>
            </Form>
          </TabPane>
        </Tabs>
      </Card>
    </div>
  );
}
