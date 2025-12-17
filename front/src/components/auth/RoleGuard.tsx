import { Navigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { UserRole } from '@/types/auth.types';
import { Result, Button } from 'antd';
import { Link } from 'react-router-dom';

interface RoleGuardProps {
  children: React.ReactNode;
  allowedRoles: UserRole[];
}

export const RoleGuard = ({ children, allowedRoles }: RoleGuardProps) => {
  const { user, hasAnyRole } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (!hasAnyRole(allowedRoles)) {
    return (
      <div className="flex h-screen items-center justify-center bg-background">
        <Result
          status="403"
          title="Acceso Denegado"
          subTitle="No tienes permisos para acceder a esta página"
          extra={
            <Link to="/dashboard">
              <Button type="primary">Volver al Dashboard</Button>
            </Link>
          }
        />
      </div>
    );
  }

  return <>{children}</>;
};
