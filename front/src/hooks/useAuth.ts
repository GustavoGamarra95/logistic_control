import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { useAuthStore } from '@/store/authStore';
import { authApi } from '@/api/auth.api';
import { LoginRequest, RegisterRequest, UserRole } from '@/types/auth.types';
import { message } from 'antd';

export const useAuth = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { user, isAuthenticated, setAuth, setUser, logout: storeLogout } = useAuthStore();

  // Fetch current user details
  const { data: userData } = useQuery({
    queryKey: ['currentUser'],
    queryFn: authApi.getCurrentUser,
    enabled: isAuthenticated && !!localStorage.getItem('accessToken'),
    retry: false,
  });

  // Update user when query succeeds
  useEffect(() => {
    if (userData && user !== userData) {
      setUser(userData as any);
    }
  }, [userData, user, setUser]);

  // Login mutation
  const loginMutation = useMutation({
    mutationFn: (credentials: LoginRequest) => authApi.login(credentials),
    onSuccess: (data) => {
      setAuth(data);
      message.success(`Bienvenido, ${data.username}`);
      navigate('/dashboard');
      
      // Fetch full user details
      queryClient.invalidateQueries({ queryKey: ['currentUser'] });
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al iniciar sesión';
      message.error(errorMsg);
    },
  });

  // Register mutation
  const registerMutation = useMutation({
    mutationFn: (data: RegisterRequest) => authApi.register(data),
    onSuccess: (data) => {
      message.success('Registro exitoso. Por favor, inicia sesión');
      navigate('/login');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al registrarse';
      message.error(errorMsg);
    },
  });

  const logout = () => {
    storeLogout();
    queryClient.clear();
    message.info('Sesión cerrada');
    navigate('/login');
  };

  const hasRole = (role: UserRole): boolean => {
    return user?.roles.includes(role) || false;
  };

  const hasAnyRole = (roles: UserRole[]): boolean => {
    return roles.some(role => user?.roles.includes(role)) || false;
  };

  return {
    user: user,
    isAuthenticated,
    isLoading: loginMutation.isPending || registerMutation.isPending,
    login: loginMutation.mutate,
    register: registerMutation.mutate,
    logout,
    hasRole,
    hasAnyRole,
  };
};
