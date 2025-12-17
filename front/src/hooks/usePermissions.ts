import { UserRole } from '@/types/auth.types';
import { useAuth } from './useAuth';

interface PermissionConfig {
  allowedRoles: UserRole[];
}

const modulePermissions: Record<string, PermissionConfig> = {
  clientes: {
    allowedRoles: ['ADMIN', 'OPERADOR', 'FINANZAS', 'CLIENTE'],
  },
  pedidos: {
    allowedRoles: ['ADMIN', 'OPERADOR', 'DEPOSITO', 'CLIENTE'],
  },
  contenedores: {
    allowedRoles: ['ADMIN', 'OPERADOR', 'DEPOSITO'],
  },
  productos: {
    allowedRoles: ['ADMIN', 'OPERADOR', 'DEPOSITO'],
  },
  inventario: {
    allowedRoles: ['ADMIN', 'OPERADOR', 'DEPOSITO', 'CLIENTE'],
  },
  facturas: {
    allowedRoles: ['ADMIN', 'FINANZAS', 'CLIENTE'],
  },
  proveedores: {
    allowedRoles: ['ADMIN', 'OPERADOR', 'FINANZAS'],
  },
  usuarios: {
    allowedRoles: ['ADMIN'],
  },
};

export const usePermissions = () => {
  const { hasAnyRole, user } = useAuth();

  const canAccess = (module: keyof typeof modulePermissions): boolean => {
    const config = modulePermissions[module];
    return config ? hasAnyRole(config.allowedRoles) : false;
  };

  const isAdmin = (): boolean => {
    return (user as any)?.roles?.includes('ADMIN') || false;
  };

  const isOperador = (): boolean => {
    return (user as any)?.roles?.includes('OPERADOR') || false;
  };

  const isFinanzas = (): boolean => {
    return (user as any)?.roles?.includes('FINANZAS') || false;
  };

  const isDeposito = (): boolean => {
    return (user as any)?.roles?.includes('DEPOSITO') || false;
  };

  const isCliente = (): boolean => {
    return (user as any)?.roles?.includes('CLIENTE') || false;
  };

  return {
    canAccess,
    isAdmin,
    isOperador,
    isFinanzas,
    isDeposito,
    isCliente,
  };
};
