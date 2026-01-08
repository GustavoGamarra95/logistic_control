import { Card, Row, Col, Statistic, Table, Tag, Space, Alert } from 'antd';
import {
  ShoppingOutlined,
  FileTextOutlined,
  InboxOutlined,
  UserOutlined,
  TeamOutlined,
  RiseOutlined,
  FallOutlined,
  WarningOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
} from '@ant-design/icons';
import { PageHeader } from '@/components/common/PageHeader';
import { usePedidos } from '@/hooks/usePedidos';
import { useFacturas } from '@/hooks/useFacturas';
import { useInventario } from '@/hooks/useInventario';
import { useClientes } from '@/hooks/useClientes';
import { useProveedores } from '@/hooks/useProveedores';
import { formatCurrency, formatDate } from '@/utils/format';
import { useMemo } from 'react';
import type { ColumnsType } from 'antd/es/table';
import { Pedido, EstadoPedido } from '@/types/pedido.types';
import { Factura, EstadoFactura } from '@/types/factura.types';
import { Inventario } from '@/types/inventario.types';
import {
  BarChart,
  Bar,
  LineChart,
  Line,
  PieChart,
  Pie,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  Cell,
} from 'recharts';

const DashboardPage = () => {
  // Fetch data using existing hooks
  const { pedidos, isLoading: loadingPedidos } = usePedidos({ page: 0, size: 10 });
  const { facturas, isLoading: loadingFacturas } = useFacturas({ page: 0, size: 10 });
  const { inventario, isLoading: loadingInventario } = useInventario({ page: 0, size: 100 });
  const { clientes, isLoading: loadingClientes } = useClientes({ page: 0, size: 1000 });
  const { proveedores, isLoading: loadingProveedores } = useProveedores({ page: 0, size: 1000 });

  // Calculate KPIs
  const kpis = useMemo(() => {
    // Pedidos KPIs
    const totalPedidos = pedidos?.length || 0;
    const pedidosEnTransito = pedidos?.filter(p => p.estado === 'EN_TRANSITO').length || 0;
    const pedidosEntregados = pedidos?.filter(p => p.estado === 'ENTREGADO').length || 0;
    const pedidosRegistrados = pedidos?.filter(p => p.estado === 'REGISTRADO').length || 0;

    // Facturas KPIs
    const totalFacturas = facturas?.length || 0;
    const facturasAprobadas = facturas?.filter(f => f.estado === 'APROBADA').length || 0;
    const facturasPendientes = facturas?.filter(f => f.estado === 'BORRADOR' || f.estado === 'EMITIDA').length || 0;
    const totalRevenue = facturas?.reduce((sum, f) => sum + (f.total || 0), 0) || 0;

    // Inventario KPIs
    const totalInventario = inventario?.length || 0;
    const cantidadDisponible = inventario?.reduce((sum, i) => sum + (i.cantidadDisponible || 0), 0) || 0;
    const cantidadReservada = inventario?.reduce((sum, i) => sum + (i.cantidadReservada || 0), 0) || 0;
    const itemsConStockBajo = inventario?.filter(i => i.cantidadDisponible < 10 && i.cantidadDisponible > 0).length || 0;
    const itemsSinStock = inventario?.filter(i => i.cantidadDisponible === 0).length || 0;

    // Clientes y Proveedores KPIs
    const totalClientes = clientes?.length || 0;
    const clientesActivos = clientes?.filter(c => c.activo).length || 0;
    const totalProveedores = proveedores?.length || 0;
    const proveedoresActivos = proveedores?.filter(p => p.activo).length || 0;

    return {
      pedidos: { total: totalPedidos, enTransito: pedidosEnTransito, entregados: pedidosEntregados, registrados: pedidosRegistrados },
      facturas: { total: totalFacturas, aprobadas: facturasAprobadas, pendientes: facturasPendientes, revenue: totalRevenue },
      inventario: { total: totalInventario, disponible: cantidadDisponible, reservada: cantidadReservada, stockBajo: itemsConStockBajo, sinStock: itemsSinStock },
      clientes: { total: totalClientes, activos: clientesActivos },
      proveedores: { total: totalProveedores, activos: proveedoresActivos },
    };
  }, [pedidos, facturas, inventario, clientes, proveedores]);

  // Recent pedidos columns
  const pedidosColumns: ColumnsType<Pedido> = [
    {
      title: 'Tracking',
      dataIndex: 'codigoTracking',
      key: 'codigoTracking',
      render: (codigo: string) => <span className="font-mono text-xs">{codigo}</span>,
    },
    {
      title: 'Cliente',
      dataIndex: 'clienteId',
      key: 'clienteId',
    },
    {
      title: 'Estado',
      dataIndex: 'estado',
      key: 'estado',
      render: (estado: EstadoPedido) => {
        const colors: Record<EstadoPedido, string> = {
          REGISTRADO: 'blue',
          EN_TRANSITO: 'orange',
          RECIBIDO: 'green',
          EN_ADUANA: 'purple',
          LIBERADO: 'cyan',
          EN_DEPOSITO: 'geekblue',
          EN_REPARTO: 'gold',
          ENTREGADO: 'success',
          CANCELADO: 'error',
          DEVUELTO: 'red',
        };
        return <Tag color={colors[estado]}>{estado}</Tag>;
      },
    },
    {
      title: 'Fecha',
      dataIndex: 'fechaRegistro',
      key: 'fechaRegistro',
      render: (fecha: string) => formatDate(fecha),
    },
  ];

  // Recent facturas columns
  const facturasColumns: ColumnsType<Factura> = [
    {
      title: 'Número',
      dataIndex: 'numeroFactura',
      key: 'numeroFactura',
      render: (numero: string) => <span className="font-mono text-xs">{numero}</span>,
    },
    {
      title: 'Cliente',
      dataIndex: 'clienteRazonSocial',
      key: 'clienteRazonSocial',
      ellipsis: true,
    },
    {
      title: 'Estado',
      dataIndex: 'estado',
      key: 'estado',
      render: (estado: EstadoFactura) => {
        const colors: Record<EstadoFactura, string> = {
          BORRADOR: 'default',
          EMITIDA: 'blue',
          ENVIADA_SIFEN: 'processing',
          APROBADA: 'success',
          RECHAZADA: 'error',
          ANULADA: 'default',
          PAGADA: 'success',
          VENCIDA: 'warning',
        };
        return <Tag color={colors[estado]}>{estado}</Tag>;
      },
    },
    {
      title: 'Total',
      dataIndex: 'total',
      key: 'total',
      align: 'right',
      render: (total: number, record) => formatCurrency(total, record.moneda),
    },
  ];

  // Low stock items columns
  const lowStockColumns: ColumnsType<Inventario> = [
    {
      title: 'Producto',
      dataIndex: 'productoDescripcion',
      key: 'productoDescripcion',
      ellipsis: true,
    },
    {
      title: 'Ubicación',
      dataIndex: 'ubicacion',
      key: 'ubicacion',
      render: (ubicacion: string) => <span className="font-mono text-xs">{ubicacion}</span>,
    },
    {
      title: 'Disponible',
      dataIndex: 'cantidadDisponible',
      key: 'cantidadDisponible',
      align: 'right',
      render: (cantidad: number) => (
        <Tag color={cantidad === 0 ? 'red' : cantidad < 10 ? 'orange' : 'green'}>
          {cantidad}
        </Tag>
      ),
    },
  ];

  const lowStockItems = useMemo(() => {
    return inventario?.filter(i => i.cantidadDisponible < 10).slice(0, 5) || [];
  }, [inventario]);

  // Chart data preparation
  const pedidosChartData = useMemo(() => {
    const statusCounts: Record<EstadoPedido, number> = {
      REGISTRADO: 0,
      EN_TRANSITO: 0,
      RECIBIDO: 0,
      EN_ADUANA: 0,
      LIBERADO: 0,
      EN_DEPOSITO: 0,
      EN_REPARTO: 0,
      ENTREGADO: 0,
      CANCELADO: 0,
      DEVUELTO: 0,
    };

    pedidos?.forEach(p => {
      statusCounts[p.estado] = (statusCounts[p.estado] || 0) + 1;
    });

    return Object.entries(statusCounts)
      .filter(([_, count]) => count > 0)
      .map(([status, count]) => ({
        estado: status,
        cantidad: count,
      }));
  }, [pedidos]);

  const facturasStatusChartData = useMemo(() => {
    const statusCounts: Partial<Record<EstadoFactura, number>> = {};

    facturas?.forEach(f => {
      statusCounts[f.estado] = (statusCounts[f.estado] || 0) + 1;
    });

    return Object.entries(statusCounts).map(([status, count]) => ({
      name: status,
      value: count,
    }));
  }, [facturas]);

  // Simulated revenue trend data (last 6 months)
  const revenueTrendData = useMemo(() => {
    const months = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'];
    const baseRevenue = kpis.facturas.revenue / 6;

    return months.map((month, index) => ({
      mes: month,
      ingresos: Math.floor(baseRevenue * (0.7 + Math.random() * 0.6)),
    }));
  }, [kpis.facturas.revenue]);

  // Inventory levels data
  const inventarioChartData = useMemo(() => {
    return [
      { name: 'Disponible', value: kpis.inventario.disponible, color: '#52c41a' },
      { name: 'Reservado', value: kpis.inventario.reservada, color: '#faad14' },
      { name: 'Sin Stock', value: kpis.inventario.sinStock * 10, color: '#ff4d4f' },
    ].filter(item => item.value > 0);
  }, [kpis.inventario]);

  // Colors for pie chart
  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8', '#82ca9d', '#ffc658'];

  return (
    <div>
      <PageHeader
        title="Dashboard"
        subtitle="Panel de control y métricas del sistema"
      />

      <Space direction="vertical" size="large" className="w-full">
        {/* Main KPI Cards */}
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="Pedidos Activos"
                value={kpis.pedidos.total}
                prefix={<ShoppingOutlined />}
                suffix={
                  <Space className="text-sm font-normal">
                    <Tag color="orange" icon={<ClockCircleOutlined />}>
                      {kpis.pedidos.enTransito} en tránsito
                    </Tag>
                  </Space>
                }
                loading={loadingPedidos}
              />
              <div className="mt-2 text-xs text-gray-500">
                <CheckCircleOutlined className="mr-1" />
                {kpis.pedidos.entregados} entregados
              </div>
            </Card>
          </Col>

          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="Facturas"
                value={kpis.facturas.total}
                prefix={<FileTextOutlined />}
                suffix={
                  <Space className="text-sm font-normal">
                    <Tag color="success">{kpis.facturas.aprobadas} aprobadas</Tag>
                  </Space>
                }
                loading={loadingFacturas}
              />
              <div className="mt-2 text-xs text-gray-500">
                <RiseOutlined className="mr-1" />
                {formatCurrency(kpis.facturas.revenue, 'PYG')}
              </div>
            </Card>
          </Col>

          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="Inventario"
                value={kpis.inventario.total}
                prefix={<InboxOutlined />}
                suffix={
                  <Space className="text-sm font-normal">
                    <Tag color="green">{kpis.inventario.disponible} unidades</Tag>
                  </Space>
                }
                loading={loadingInventario}
              />
              <div className="mt-2 text-xs text-gray-500">
                {kpis.inventario.stockBajo > 0 && (
                  <>
                    <WarningOutlined className="mr-1 text-orange-500" />
                    {kpis.inventario.stockBajo} con stock bajo
                  </>
                )}
              </div>
            </Card>
          </Col>

          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="Clientes"
                value={kpis.clientes.total}
                prefix={<UserOutlined />}
                suffix={
                  <Space className="text-sm font-normal">
                    <Tag color="blue">{kpis.clientes.activos} activos</Tag>
                  </Space>
                }
                loading={loadingClientes}
              />
              <div className="mt-2 text-xs text-gray-500">
                <TeamOutlined className="mr-1" />
                {kpis.proveedores.activos} proveedores activos
              </div>
            </Card>
          </Col>
        </Row>

        {/* Alert for stock issues */}
        {kpis.inventario.sinStock > 0 && (
          <Alert
            message="Alerta de Inventario"
            description={`Hay ${kpis.inventario.sinStock} productos sin stock y ${kpis.inventario.stockBajo} con stock bajo. Revisar inventario.`}
            type="warning"
            showIcon
            icon={<WarningOutlined />}
          />
        )}

        {/* Recent Activity Tables */}
        <Row gutter={[16, 16]}>
          <Col xs={24} lg={12}>
            <Card
              title="Pedidos Recientes"
              extra={<a href="/pedidos">Ver todos</a>}
              size="small"
            >
              <Table
                columns={pedidosColumns}
                dataSource={pedidos?.slice(0, 5)}
                rowKey="id"
                pagination={false}
                size="small"
                loading={loadingPedidos}
              />
            </Card>
          </Col>

          <Col xs={24} lg={12}>
            <Card
              title="Facturas Recientes"
              extra={<a href="/facturas">Ver todas</a>}
              size="small"
            >
              <Table
                columns={facturasColumns}
                dataSource={facturas?.slice(0, 5)}
                rowKey="id"
                pagination={false}
                size="small"
                loading={loadingFacturas}
              />
            </Card>
          </Col>
        </Row>

        {/* Low Stock Alert Table */}
        {lowStockItems.length > 0 && (
          <Row gutter={[16, 16]}>
            <Col xs={24}>
              <Card
                title={
                  <Space>
                    <WarningOutlined className="text-orange-500" />
                    <span>Productos con Stock Bajo</span>
                  </Space>
                }
                extra={<a href="/inventario">Ver inventario completo</a>}
                size="small"
              >
                <Table
                  columns={lowStockColumns}
                  dataSource={lowStockItems}
                  rowKey="id"
                  pagination={false}
                  size="small"
                  loading={loadingInventario}
                />
              </Card>
            </Col>
          </Row>
        )}

        {/* Status Distribution Cards */}
        <Row gutter={[16, 16]}>
          <Col xs={24} md={12}>
            <Card title="Distribución de Pedidos" size="small">
              <Space direction="vertical" className="w-full">
                <div className="flex justify-between items-center">
                  <span>Registrados</span>
                  <Tag color="blue">{kpis.pedidos.registrados}</Tag>
                </div>
                <div className="flex justify-between items-center">
                  <span>En Tránsito</span>
                  <Tag color="orange">{kpis.pedidos.enTransito}</Tag>
                </div>
                <div className="flex justify-between items-center">
                  <span>Entregados</span>
                  <Tag color="green">{kpis.pedidos.entregados}</Tag>
                </div>
              </Space>
            </Card>
          </Col>

          <Col xs={24} md={12}>
            <Card title="Estado de Facturas" size="small">
              <Space direction="vertical" className="w-full">
                <div className="flex justify-between items-center">
                  <span>Pendientes</span>
                  <Tag color="orange">{kpis.facturas.pendientes}</Tag>
                </div>
                <div className="flex justify-between items-center">
                  <span>Aprobadas</span>
                  <Tag color="green">{kpis.facturas.aprobadas}</Tag>
                </div>
                <div className="flex justify-between items-center">
                  <span>Ingresos Totales</span>
                  <span className="font-semibold">{formatCurrency(kpis.facturas.revenue, 'PYG')}</span>
                </div>
              </Space>
            </Card>
          </Col>
        </Row>

        {/* Charts Section */}
        <Row gutter={[16, 16]}>
          {/* Revenue Trend Line Chart */}
          <Col xs={24} lg={12}>
            <Card title="Tendencia de Ingresos" size="small">
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={revenueTrendData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="mes" />
                  <YAxis tickFormatter={(value) => `${(value / 1000000).toFixed(1)}M`} />
                  <Tooltip
                    formatter={(value: number) => formatCurrency(value, 'PYG')}
                    labelStyle={{ color: '#000' }}
                  />
                  <Legend />
                  <Line
                    type="monotone"
                    dataKey="ingresos"
                    stroke="#0066CC"
                    strokeWidth={2}
                    name="Ingresos"
                    dot={{ fill: '#0066CC', r: 4 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            </Card>
          </Col>

          {/* Orders by Status Bar Chart */}
          <Col xs={24} lg={12}>
            <Card title="Pedidos por Estado" size="small">
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={pedidosChartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="estado" angle={-45} textAnchor="end" height={80} />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="cantidad" fill="#1890ff" name="Cantidad" />
                </BarChart>
              </ResponsiveContainer>
            </Card>
          </Col>

          {/* Invoice Status Pie Chart */}
          <Col xs={24} lg={12}>
            <Card title="Distribución de Estados - Facturas" size="small">
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={facturasStatusChartData}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {facturasStatusChartData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </Card>
          </Col>

          {/* Inventory Levels Area Chart */}
          <Col xs={24} lg={12}>
            <Card title="Niveles de Inventario" size="small">
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={inventarioChartData}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, value }) => `${name}: ${value}`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {inventarioChartData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </Card>
          </Col>
        </Row>
      </Space>
    </div>
  );
};

export default DashboardPage;
