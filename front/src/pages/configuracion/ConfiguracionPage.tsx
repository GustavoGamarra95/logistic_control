import {
  Card,
  Form,
  Switch,
  Select,
  Divider,
  Typography,
  Space,
  Row,
  Col,
  Button,
  message,
  Tabs,
  Table,
  Input,
  InputNumber,
  Tag,
} from 'antd';
import {
  BellOutlined,
  GlobalOutlined,
  BgColorsOutlined,
  SettingOutlined,
  SaveOutlined,
  UserOutlined,
  PlusOutlined,
  SearchOutlined,
  EditOutlined,
  LockOutlined,
  UnlockOutlined,
  FileTextOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  SyncOutlined,
  ApiOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useTranslation } from 'react-i18next';
import { ThemeToggle } from '@/components/common/ThemeToggle';
import { LanguageSelector } from '@/components/common/LanguageSelector';
import { useAuthStore } from '@/store/authStore';
import { useUsuarios } from '@/hooks/useUsuarios';
import { usePagination } from '@/hooks/usePagination';
import { useDebounce } from '@/hooks/useDebounce';
import { Usuario } from '@/types/usuario.types';
import { sifenApi, SifenConfig, SifenEstadoResponse } from '@/api/sifen.api';
import { UserEditModal } from '@/components/usuarios/UserEditModal';
import { useState, useEffect } from 'react';

const { Title, Paragraph } = Typography;

interface ConfigFormValues {
  notificationsEnabled: boolean;
  emailNotifications: boolean;
  soundEnabled: boolean;
  autoSave: boolean;
  pageSize: number;
  dateFormat: string;
  currency: string;
}

export default function ConfiguracionPage() {
  const { t } = useTranslation();
  const [form] = Form.useForm<ConfigFormValues>();
  const [sifenForm] = Form.useForm<SifenConfig>();
  const [loading, setLoading] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [editingUser, setEditingUser] = useState<Usuario | null>(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [sifenConfig, setSifenConfig] = useState<SifenConfig | null>(null);
  const [sifenEstado, setSifenEstado] = useState<SifenEstadoResponse | null>(null);
  const [verificandoSifen, setVerificandoSifen] = useState(false);

  // Get current user role
  const user = useAuthStore((state) => state.user);
  const isAdmin = user?.roles?.includes('ADMIN') ?? false;

  // Users management hooks
  const debouncedSearch = useDebounce(searchText, 300);
  const { page, size, handlePageChange, handleSizeChange } = usePagination();

  const {
    usuarios,
    pagination,
    isLoading: isLoadingUsuarios,
    bloquearUsuario,
    desbloquearUsuario,
    activarUsuario,
    desactivarUsuario,
    updateUsuario,
    resetPassword,
  } = useUsuarios({
    page,
    size,
    search: debouncedSearch,
  });

  const handleSave = async (values: ConfigFormValues) => {
    setLoading(true);
    try {
      // TODO: Implement API call to save settings
      console.log('Saving settings:', values);
      localStorage.setItem('userSettings', JSON.stringify(values));
      message.success('Configuración guardada exitosamente');
    } catch (error) {
      message.error('Error al guardar la configuración');
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    form.resetFields();
    message.info('Configuración restablecida a valores predeterminados');
  };

  // Load saved settings from localStorage
  const loadSavedSettings = (): ConfigFormValues => {
    const saved = localStorage.getItem('userSettings');
    if (saved) {
      return JSON.parse(saved);
    }
    return {
      notificationsEnabled: true,
      emailNotifications: true,
      soundEnabled: true,
      autoSave: true,
      pageSize: 10,
      dateFormat: 'DD/MM/YYYY',
      currency: 'PYG',
    };
  };

  // User actions handlers
  const handleEditUser = (usuario: Usuario) => {
    setEditingUser(usuario);
    setIsEditModalOpen(true);
  };

  const handleCloseEditModal = () => {
    setEditingUser(null);
    setIsEditModalOpen(false);
  };

  const handleUpdateUser = (id: number, data: any) => {
    updateUsuario({ id, data });
  };

  const handleResetPassword = (id: number, newPassword: string) => {
    resetPassword({ id, newPassword });
  };

  const handleToggleBloqueo = (usuario: Usuario) => {
    const bloqueado = !usuario.accountNonLocked;
    if (bloqueado) {
      desbloquearUsuario(usuario.id);
    } else {
      bloquearUsuario(usuario.id);
    }
  };

  const handleToggleActivo = (usuario: Usuario) => {
    if (usuario.enabled) {
      desactivarUsuario(usuario.id);
    } else {
      activarUsuario(usuario.id);
    }
  };

  // SIFEN handlers
  useEffect(() => {
    if (isAdmin) {
      loadSifenConfig();
    }
  }, [isAdmin]);

  const loadSifenConfig = async () => {
    try {
      const config = await sifenApi.getConfig();
      setSifenConfig(config);
      sifenForm.setFieldsValue(config);
    } catch (error) {
      console.error('Error loading SIFEN config:', error);
    }
  };

  const handleVerificarConexion = async () => {
    setVerificandoSifen(true);
    try {
      const estado = await sifenApi.verificarConexion();
      setSifenEstado(estado);
      if (estado.conectado) {
        message.success(estado.mensaje);
      } else {
        message.error(estado.mensaje);
      }
    } catch (error) {
      message.error('Error al verificar conexión con SIFEN');
      setSifenEstado({
        conectado: false,
        ambiente: sifenConfig?.ambiente || 'test',
        mensaje: 'No se pudo establecer conexión con SIFEN',
        timestamp: new Date().toISOString(),
      });
    } finally {
      setVerificandoSifen(false);
    }
  };

  const handleSaveSifenConfig = async (values: SifenConfig) => {
    setLoading(true);
    try {
      await sifenApi.updateConfig(values);
      message.success('Configuración de SIFEN guardada exitosamente');
      loadSifenConfig();
    } catch (error) {
      message.error('Error al guardar configuración de SIFEN');
    } finally {
      setLoading(false);
    }
  };

  // Users table columns
  const usuariosColumns: ColumnsType<Usuario> = [
    {
      title: 'Username',
      dataIndex: 'username',
      key: 'username',
      width: 150,
    },
    {
      title: 'Nombre',
      dataIndex: 'nombre',
      key: 'nombre',
      ellipsis: true,
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      width: 200,
      ellipsis: true,
    },
    {
      title: 'Roles',
      dataIndex: 'roles',
      key: 'roles',
      width: 250,
      render: (roles: string[]) => (
        <Space size="small" wrap>
          {(roles || []).map(role => <Tag key={role} color="blue">{role}</Tag>)}
        </Space>
      ),
    },
    {
      title: 'Estado',
      key: 'estado',
      width: 150,
      render: (_, record) => {
        const bloqueado = !record.accountNonLocked;
        return (
          <Space wrap>
            {record.enabled ? (
              <Tag color="green">Activo</Tag>
            ) : (
              <Tag color="orange">Inactivo</Tag>
            )}
            {bloqueado && <Tag color="red">Bloqueado</Tag>}
          </Space>
        );
      },
    },
    {
      title: 'Activo',
      dataIndex: 'enabled',
      key: 'enabled',
      width: 80,
      render: (enabled: boolean, record: Usuario) => (
        <Switch
          checked={enabled}
          size="small"
          onChange={() => handleToggleActivo(record)}
        />
      ),
    },
    {
      title: 'Acciones',
      key: 'actions',
      width: 120,
      render: (_, record) => {
        const bloqueado = !record.accountNonLocked;
        return (
          <Space size="small">
            <Button
              type="text"
              icon={<EditOutlined />}
              size="small"
              title="Editar"
              onClick={() => handleEditUser(record)}
            />
            <Button
              type="text"
              icon={bloqueado ? <UnlockOutlined /> : <LockOutlined />}
              size="small"
              title={bloqueado ? 'Desbloquear' : 'Bloquear'}
              onClick={() => handleToggleBloqueo(record)}
              danger={!bloqueado}
            />
          </Space>
        );
      },
    },
  ];

  const tabItems = [
    {
      key: 'general',
      label: (
        <span>
          <SettingOutlined /> {t('configuracion.tabs.general')}
        </span>
      ),
      children: (
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSave}
          initialValues={loadSavedSettings()}
        >
          <Row gutter={[24, 24]}>
            {/* Apariencia */}
            <Col xs={24} lg={12}>
              <Card
                title={
                  <Space>
                    <BgColorsOutlined />
                    <span>{t('configuracion.apariencia.title')}</span>
                  </Space>
                }
              >
                <Space vertical size="large" style={{ width: '100%' }}>
                  <div>
                    <Title level={5}>{t('configuracion.apariencia.theme')}</Title>
                    <Paragraph type="secondary">
                      {t('configuracion.apariencia.themeDescription')}
                    </Paragraph>
                    <ThemeToggle />
                  </div>

                  <Divider />

                  <div>
                    <Title level={5}>{t('configuracion.apariencia.language')}</Title>
                    <Paragraph type="secondary">
                      {t('configuracion.apariencia.languageDescription')}
                    </Paragraph>
                    <LanguageSelector />
                  </div>
                </Space>
              </Card>
            </Col>

            {/* Notificaciones */}
            <Col xs={24} lg={12}>
              <Card
                title={
                  <Space>
                    <BellOutlined />
                    <span>{t('configuracion.notificaciones.title')}</span>
                  </Space>
                }
              >
                <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                    <div>
                      <div style={{ fontWeight: 500 }}>{t('configuracion.notificaciones.push')}</div>
                      <Paragraph type="secondary" style={{ margin: 0, fontSize: 12 }}>
                        {t('configuracion.notificaciones.pushDescription')}
                      </Paragraph>
                    </div>
                    <Form.Item
                      name="notificationsEnabled"
                      valuePropName="checked"
                      style={{ marginBottom: 0 }}
                    >
                      <Switch />
                    </Form.Item>
                  </div>

                  <Divider style={{ margin: '8px 0' }} />

                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                    <div>
                      <div style={{ fontWeight: 500 }}>{t('configuracion.notificaciones.email')}</div>
                      <Paragraph type="secondary" style={{ margin: 0, fontSize: 12 }}>
                        {t('configuracion.notificaciones.emailDescription')}
                      </Paragraph>
                    </div>
                    <Form.Item
                      name="emailNotifications"
                      valuePropName="checked"
                      style={{ marginBottom: 0 }}
                    >
                      <Switch />
                    </Form.Item>
                  </div>

                  <Divider style={{ margin: '8px 0' }} />

                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <div>
                      <div style={{ fontWeight: 500 }}>{t('configuracion.notificaciones.sound')}</div>
                      <Paragraph type="secondary" style={{ margin: 0, fontSize: 12 }}>
                        {t('configuracion.notificaciones.soundDescription')}
                      </Paragraph>
                    </div>
                    <Form.Item
                      name="soundEnabled"
                      valuePropName="checked"
                      style={{ marginBottom: 0 }}
                    >
                      <Switch />
                    </Form.Item>
                  </div>
                </Space>
              </Card>
            </Col>

            {/* General */}
            <Col xs={24} lg={12}>
              <Card
                title={
                  <Space>
                    <GlobalOutlined />
                    <span>{t('configuracion.general.title')}</span>
                  </Space>
                }
              >
                <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                    <div>
                      <div style={{ fontWeight: 500 }}>{t('configuracion.general.autoSave')}</div>
                      <Paragraph type="secondary" style={{ margin: 0, fontSize: 12 }}>
                        {t('configuracion.general.autoSaveDescription')}
                      </Paragraph>
                    </div>
                    <Form.Item
                      name="autoSave"
                      valuePropName="checked"
                      style={{ marginBottom: 0 }}
                    >
                      <Switch />
                    </Form.Item>
                  </div>

                  <Divider style={{ margin: '8px 0' }} />

                  <Form.Item
                    label="Elementos por página"
                    name="pageSize"
                    style={{ marginBottom: 8 }}
                  >
                    <Select>
                      <Select.Option value={10}>10</Select.Option>
                      <Select.Option value={20}>20</Select.Option>
                      <Select.Option value={50}>50</Select.Option>
                      <Select.Option value={100}>100</Select.Option>
                    </Select>
                  </Form.Item>

                  <Form.Item
                    label="Formato de fecha"
                    name="dateFormat"
                    style={{ marginBottom: 8 }}
                  >
                    <Select>
                      <Select.Option value="DD/MM/YYYY">DD/MM/YYYY</Select.Option>
                      <Select.Option value="MM/DD/YYYY">MM/DD/YYYY</Select.Option>
                      <Select.Option value="YYYY-MM-DD">YYYY-MM-DD</Select.Option>
                    </Select>
                  </Form.Item>

                  <Form.Item
                    label="Moneda predeterminada"
                    name="currency"
                    style={{ marginBottom: 0 }}
                  >
                    <Select>
                      <Select.Option value="PYG">Guaraníes (PYG)</Select.Option>
                      <Select.Option value="USD">Dólares (USD)</Select.Option>
                      <Select.Option value="EUR">Euros (EUR)</Select.Option>
                    </Select>
                  </Form.Item>
                </Space>
              </Card>
            </Col>
          </Row>

          <Divider />

          <Space>
            <Button type="primary" htmlType="submit" icon={<SaveOutlined />} loading={loading}>
              Guardar Cambios
            </Button>
            <Button onClick={handleReset}>Restablecer</Button>
          </Space>
        </Form>
      ),
    },
  ];

  // Add Facturacion tab only for admins
  if (isAdmin) {
    tabItems.push({
      key: 'facturacion',
      label: (
        <span>
          <FileTextOutlined /> {t('configuracion.tabs.facturacion')}
        </span>
      ),
      children: (
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          {/* Estado de conexión SIFEN */}
          <Card>
            <Space direction="vertical" size="middle" style={{ width: '100%' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <Title level={4} style={{ margin: 0 }}>
                    <ApiOutlined /> Estado de Conexión SIFEN
                  </Title>
                  <Paragraph type="secondary" style={{ margin: 0 }}>
                    Sistema Integrado de Facturación Electrónica Nacional - Paraguay
                  </Paragraph>
                </div>
                <Button
                  type="primary"
                  icon={<SyncOutlined spin={verificandoSifen} />}
                  onClick={handleVerificarConexion}
                  loading={verificandoSifen}
                >
                  Verificar Conexión
                </Button>
              </div>

              {sifenEstado && (
                <Card size="small" style={{ background: sifenEstado.conectado ? '#f6ffed' : '#fff1f0' }}>
                  <Space>
                    {sifenEstado.conectado ? (
                      <CheckCircleOutlined style={{ fontSize: 24, color: '#52c41a' }} />
                    ) : (
                      <CloseCircleOutlined style={{ fontSize: 24, color: '#ff4d4f' }} />
                    )}
                    <div>
                      <div style={{ fontWeight: 500 }}>
                        {sifenEstado.conectado ? 'Conectado' : 'Desconectado'}
                      </div>
                      <div style={{ fontSize: 12, color: '#666' }}>
                        {sifenEstado.mensaje}
                      </div>
                      <div style={{ fontSize: 11, color: '#999' }}>
                        Ambiente: {sifenEstado.ambiente === 'prod' ? 'Producción' : 'Pruebas'} • {new Date(sifenEstado.timestamp).toLocaleString('es-PY')}
                      </div>
                    </div>
                  </Space>
                </Card>
              )}
            </Space>
          </Card>

          {/* Configuración SIFEN */}
          <Card title={<span><SettingOutlined /> Configuración de Facturación Electrónica</span>}>
            <Form
              form={sifenForm}
              layout="vertical"
              onFinish={handleSaveSifenConfig}
            >
              <Row gutter={[24, 16]}>
                <Col xs={24} md={12}>
                  <Form.Item
                    label="Ambiente"
                    name="ambiente"
                    rules={[{ required: true }]}
                  >
                    <Select>
                      <Select.Option value="test">Pruebas (Test)</Select.Option>
                      <Select.Option value="prod">Producción</Select.Option>
                    </Select>
                  </Form.Item>
                </Col>

                <Col xs={24} md={12}>
                  <Form.Item
                    label="Timbrado"
                    name="timbrado"
                    rules={[{ required: true, message: 'Timbrado es requerido' }]}
                  >
                    <Input placeholder="12345678" />
                  </Form.Item>
                </Col>

                <Col xs={24} md={12}>
                  <Form.Item
                    label="RUC Emisor"
                    name="rucEmisor"
                    rules={[{ required: true, message: 'RUC es requerido' }]}
                  >
                    <Input placeholder="80012345-1" />
                  </Form.Item>
                </Col>

                <Col xs={24} md={12}>
                  <Form.Item
                    label="Razón Social"
                    name="razonSocialEmisor"
                    rules={[{ required: true }]}
                  >
                    <Input placeholder="Mi Empresa S.A." />
                  </Form.Item>
                </Col>

                <Col xs={24} md={12}>
                  <Form.Item label="Nombre de Fantasía" name="nombreFantasia">
                    <Input placeholder="Mi Empresa" />
                  </Form.Item>
                </Col>

                <Col xs={24} md={12}>
                  <Form.Item label="Actividad Económica" name="actividadEconomica">
                    <Input placeholder="Logística y Transporte" />
                  </Form.Item>
                </Col>

                <Col xs={24} md={8}>
                  <Form.Item
                    label="Establecimiento"
                    name="establecimiento"
                    rules={[{ required: true, len: 3 }]}
                  >
                    <Input placeholder="001" maxLength={3} />
                  </Form.Item>
                </Col>

                <Col xs={24} md={8}>
                  <Form.Item
                    label="Punto de Expedición"
                    name="puntoExpedicion"
                    rules={[{ required: true, len: 3 }]}
                  >
                    <Input placeholder="001" maxLength={3} />
                  </Form.Item>
                </Col>

                <Col xs={24} md={8}>
                  <Form.Item label="Departamento" name="departamento">
                    <Input placeholder="11 (Capital)" maxLength={2} />
                  </Form.Item>
                </Col>

                <Col xs={24} md={12}>
                  <Form.Item label="Dirección" name="direccion">
                    <Input placeholder="Av. Principal 123" />
                  </Form.Item>
                </Col>

                <Col xs={24} md={12}>
                  <Form.Item label="Ciudad" name="ciudad">
                    <Input placeholder="Asunción" />
                  </Form.Item>
                </Col>

                <Col xs={24} md={12}>
                  <Form.Item
                    label="Teléfono"
                    name="telefono"
                    rules={[{ required: true }]}
                  >
                    <Input placeholder="+595 21 123456" />
                  </Form.Item>
                </Col>

                <Col xs={24} md={12}>
                  <Form.Item
                    label="Email"
                    name="email"
                    rules={[{ required: true, type: 'email' }]}
                  >
                    <Input placeholder="facturacion@miempresa.com.py" />
                  </Form.Item>
                </Col>

                <Col xs={24} md={12}>
                  <Form.Item label="Timeout de Conexión (seg)" name="connectTimeout">
                    <InputNumber min={5} max={60} style={{ width: '100%' }} />
                  </Form.Item>
                </Col>

                <Col xs={24} md={12}>
                  <Form.Item label="Timeout de Lectura (seg)" name="readTimeout">
                    <InputNumber min={10} max={300} style={{ width: '100%' }} />
                  </Form.Item>
                </Col>

                <Col xs={24} md={12}>
                  <Form.Item label="Máximo de Reintentos" name="maxReintentos">
                    <InputNumber min={1} max={10} style={{ width: '100%' }} />
                  </Form.Item>
                </Col>

                <Col xs={24} md={12}>
                  <Form.Item name="contingenciaEnabled" valuePropName="checked">
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, height: '100%', paddingTop: 30 }}>
                      <Switch />
                      <span>Habilitar modo contingencia</span>
                    </div>
                  </Form.Item>
                </Col>
              </Row>

              <Divider />

              <Space>
                <Button type="primary" htmlType="submit" icon={<SaveOutlined />} loading={loading}>
                  Guardar Configuración SIFEN
                </Button>
                <Button onClick={() => sifenForm.resetFields()}>Restablecer</Button>
              </Space>
            </Form>
          </Card>
        </Space>
      ),
    });
  }

  // Add Users tab only for admins
  if (isAdmin) {
    tabItems.push({
      key: 'usuarios',
      label: (
        <span>
          <UserOutlined /> {t('configuracion.tabs.usuarios')}
        </span>
      ),
      children: (
        <Card>
          <Space direction="vertical" size="middle" style={{ width: '100%' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <Title level={4} style={{ margin: 0 }}>Gestión de Usuarios</Title>
                <Paragraph type="secondary" style={{ margin: 0 }}>
                  Administración de usuarios y roles del sistema
                </Paragraph>
              </div>
              <Button type="primary" icon={<PlusOutlined />}>
                Nuevo Usuario
              </Button>
            </div>

            <Input
              placeholder="Buscar por nombre, username o email..."
              prefix={<SearchOutlined />}
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              style={{ maxWidth: 400 }}
              allowClear
            />

            <Table
              columns={usuariosColumns}
              dataSource={usuarios}
              rowKey="id"
              loading={isLoadingUsuarios}
              pagination={{
                current: pagination.current,
                pageSize: pagination.pageSize,
                total: pagination.total,
                showSizeChanger: true,
                showTotal: (total) => `Total ${total} usuarios`,
                pageSizeOptions: ['10', '20', '50', '100'],
                onChange: handlePageChange,
                onShowSizeChange: handleSizeChange,
              }}
              scroll={{ x: 1000 }}
            />
          </Space>
        </Card>
      ),
    });
  }

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>
        <SettingOutlined /> {t('configuracion.title')}
      </Title>
      <Paragraph>{t('configuracion.subtitle')}</Paragraph>

      <Tabs defaultActiveKey="general" items={tabItems} />

      {/* Modal de edición de usuario */}
      <UserEditModal
        usuario={editingUser}
        open={isEditModalOpen}
        onClose={handleCloseEditModal}
        onUpdate={handleUpdateUser}
        onResetPassword={handleResetPassword}
      />
    </div>
  );
}
