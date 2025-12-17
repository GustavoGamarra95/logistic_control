import { Form, Input, Button, Card, Typography } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { LoginRequest } from '@/types/auth.types';
import { useAuth } from '@/hooks/useAuth';

const { Title, Text } = Typography;

export const LoginForm = () => {
  const { login, isLoading } = useAuth();
  const [form] = Form.useForm();

  const onFinish = (values: LoginRequest) => {
    login(values);
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-primary/10 to-accent/10 px-4">
      <Card className="w-full max-w-md shadow-lg">
        <div className="text-center mb-8">
          <Title level={2} className="!mb-2">Sistema Logístico</Title>
          <Text type="secondary">Ingresa tus credenciales para continuar</Text>
        </div>

        <Form
          form={form}
          name="login"
          onFinish={onFinish}
          layout="vertical"
          size="large"
          autoComplete="off"
        >
          <Form.Item
            name="username"
            label="Usuario"
            rules={[{ required: true, message: 'Por favor ingresa tu usuario' }]}
          >
            <Input
              prefix={<UserOutlined className="text-muted-foreground" />}
              placeholder="usuario"
            />
          </Form.Item>

          <Form.Item
            name="password"
            label="Contraseña"
            rules={[{ required: true, message: 'Por favor ingresa tu contraseña' }]}
          >
            <Input.Password
              prefix={<LockOutlined className="text-muted-foreground" />}
              placeholder="contraseña"
            />
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={isLoading}
              block
              className="!mt-4"
            >
              Iniciar Sesión
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};
