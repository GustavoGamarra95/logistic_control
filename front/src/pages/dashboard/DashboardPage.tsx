import { Row, Col, Card, Statistic } from 'antd';
import {
  UserOutlined,
  ShoppingOutlined,
  InboxOutlined,
  FileTextOutlined,
  RiseOutlined,
  FallOutlined,
} from '@ant-design/icons';
import { useAuth } from '@/hooks/useAuth';
import { PageHeader } from '@/components/common/PageHeader';
import { LineChart, Line, BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const DashboardPage = () => {
  const { user } = useAuth();

  const stats = [
    {
      title: 'Clientes Activos',
      value: 127,
      icon: <UserOutlined className="text-primary text-2xl" />,
      color: 'primary',
      prefix: <RiseOutlined className="text-accent" />,
      suffix: '+12%',
    },
    {
      title: 'Pedidos en Tránsito',
      value: 45,
      icon: <ShoppingOutlined className="text-info text-2xl" />,
      color: 'info',
      prefix: <RiseOutlined className="text-accent" />,
      suffix: '+8%',
    },
    {
      title: 'Contenedores',
      value: 23,
      icon: <InboxOutlined className="text-accent text-2xl" />,
      color: 'accent',
      prefix: <FallOutlined className="text-destructive" />,
      suffix: '-3%',
    },
    {
      title: 'Facturas Pendientes',
      value: 18,
      icon: <FileTextOutlined className="text-warning text-2xl" />,
      color: 'warning',
      prefix: <RiseOutlined className="text-accent" />,
      suffix: '+5%',
    },
  ];

  // Datos de ejemplo para gráficos
  const pedidosData = [
    { mes: 'Ene', pedidos: 65, completados: 58 },
    { mes: 'Feb', pedidos: 78, completados: 72 },
    { mes: 'Mar', pedidos: 90, completados: 85 },
    { mes: 'Abr', pedidos: 81, completados: 75 },
    { mes: 'May', pedidos: 95, completados: 89 },
    { mes: 'Jun', pedidos: 110, completados: 102 },
  ];

  const estadosPedidosData = [
    { nombre: 'En Tránsito', value: 45, color: 'hsl(var(--chart-1))' },
    { nombre: 'En Aduana', value: 23, color: 'hsl(var(--chart-3))' },
    { nombre: 'En Depósito', value: 18, color: 'hsl(var(--chart-2))' },
    { nombre: 'Entregados', value: 102, color: 'hsl(var(--chart-5))' },
  ];

  const ingresosData = [
    { mes: 'Ene', ingresos: 45000, gastos: 32000 },
    { mes: 'Feb', ingresos: 52000, gastos: 35000 },
    { mes: 'Mar', ingresos: 61000, gastos: 38000 },
    { mes: 'Abr', ingresos: 58000, gastos: 36000 },
    { mes: 'May', ingresos: 67000, gastos: 42000 },
    { mes: 'Jun', ingresos: 75000, gastos: 45000 },
  ];

  return (
    <div>
      <PageHeader
        title="Dashboard"
        subtitle={`Bienvenido, ${user?.nombre || user?.username}`}
      />

      {/* KPI Cards */}
      <Row gutter={[16, 16]} className="mb-6">
        {stats.map((stat, index) => (
          <Col xs={24} sm={12} lg={6} key={index}>
            <Card className="hover:shadow-lg transition-shadow" bordered={false}>
              <div className="flex items-center justify-between">
                <Statistic
                  title={stat.title}
                  value={stat.value}
                  valueStyle={{ color: `hsl(var(--${stat.color}))` }}
                  prefix={stat.prefix}
                  suffix={
                    <span className="text-sm text-muted-foreground ml-2">
                      {stat.suffix}
                    </span>
                  }
                />
                {stat.icon}
              </div>
            </Card>
          </Col>
        ))}
      </Row>

      {/* Gráficos */}
      <Row gutter={[16, 16]}>
        <Col xs={24} lg={12}>
          <Card title="Pedidos por Mes" className="h-96">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={pedidosData}>
                <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                <XAxis dataKey="mes" stroke="hsl(var(--muted-foreground))" />
                <YAxis stroke="hsl(var(--muted-foreground))" />
                <Tooltip
                  contentStyle={{
                    backgroundColor: 'hsl(var(--card))',
                    border: '1px solid hsl(var(--border))',
                    borderRadius: '6px',
                  }}
                />
                <Legend />
                <Line
                  type="monotone"
                  dataKey="pedidos"
                  stroke="hsl(var(--chart-1))"
                  strokeWidth={2}
                  name="Pedidos"
                />
                <Line
                  type="monotone"
                  dataKey="completados"
                  stroke="hsl(var(--chart-2))"
                  strokeWidth={2}
                  name="Completados"
                />
              </LineChart>
            </ResponsiveContainer>
          </Card>
        </Col>

        <Col xs={24} lg={12}>
          <Card title="Estados de Pedidos" className="h-96">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={estadosPedidosData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={(entry: any) =>
                    `${entry.nombre} ${(entry.percent * 100).toFixed(0)}%`
                  }
                  outerRadius={80}
                  fill="hsl(var(--chart-1))"
                  dataKey="value"
                >
                  {estadosPedidosData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip
                  contentStyle={{
                    backgroundColor: 'hsl(var(--card))',
                    border: '1px solid hsl(var(--border))',
                    borderRadius: '6px',
                  }}
                />
              </PieChart>
            </ResponsiveContainer>
          </Card>
        </Col>

        <Col xs={24}>
          <Card title="Ingresos vs Gastos" className="h-96">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={ingresosData}>
                <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                <XAxis dataKey="mes" stroke="hsl(var(--muted-foreground))" />
                <YAxis stroke="hsl(var(--muted-foreground))" />
                <Tooltip
                  contentStyle={{
                    backgroundColor: 'hsl(var(--card))',
                    border: '1px solid hsl(var(--border))',
                    borderRadius: '6px',
                  }}
                  formatter={(value: number) =>
                    new Intl.NumberFormat('es-PY', {
                      style: 'currency',
                      currency: 'PYG',
                      minimumFractionDigits: 0,
                    }).format(value)
                  }
                />
                <Legend />
                <Bar dataKey="ingresos" fill="hsl(var(--chart-2))" name="Ingresos" />
                <Bar dataKey="gastos" fill="hsl(var(--chart-4))" name="Gastos" />
              </BarChart>
            </ResponsiveContainer>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default DashboardPage;
