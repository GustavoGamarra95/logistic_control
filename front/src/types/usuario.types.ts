import { UserRole } from './auth.types';

export interface Usuario {
  id: number;
  username: string;
  email: string;
  nombre: string;
  apellido: string;
  telefono?: string;
  roles: UserRole[];
  enabled: boolean;
  accountNonExpired: boolean;
  accountNonLocked: boolean;
  credentialsNonExpired: boolean;
  lastLogin?: string;
  failedLoginAttempts: number;
  clienteId?: number;
  clienteRazonSocial?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface CreateUsuarioRequest {
  username: string;
  email: string;
  password: string;
  nombre: string;
  apellido: string;
  telefono?: string;
  roles: UserRole[];
  clienteId?: number;
}

export interface UpdateUsuarioRequest {
  email?: string;
  nombre?: string;
  apellido?: string;
  telefono?: string;
  roles?: UserRole[];
  clienteId?: number;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface ResetPasswordRequest {
  newPassword: string;
}