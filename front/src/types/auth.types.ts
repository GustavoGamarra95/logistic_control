export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  nombre: string;
  apellido: string;
  telefono?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  username: string;
  email: string;
  roles: UserRole[];
}

export interface User {
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
}

export type UserRole = 'ADMIN' | 'OPERADOR' | 'CLIENTE' | 'FINANZAS' | 'DEPOSITO';

export interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  setAuth: (authData: AuthResponse | null) => void;
  logout: () => void;
  setUser: (user: User | null) => void;
}
