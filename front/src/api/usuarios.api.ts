import apiClient from './axios-config';
import { Usuario, CreateUsuarioRequest, UpdateUsuarioRequest } from '@/types/usuario.types';
import { PaginatedResponse, QueryParams } from '@/types/api.types';

export const usuariosApi = {
  getAll: async (params?: QueryParams): Promise<PaginatedResponse<Usuario>> => {
    const response = await apiClient.get<PaginatedResponse<Usuario>>('/usuarios', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Usuario> => {
    const response = await apiClient.get<Usuario>(`/usuarios/${id}`);
    return response.data;
  },

  getByUsername: async (username: string): Promise<Usuario> => {
    const response = await apiClient.get<Usuario>(`/usuarios/username/${username}`);
    return response.data;
  },

  getByEmail: async (email: string): Promise<Usuario> => {
    const response = await apiClient.get<Usuario>(`/usuarios/email/${email}`);
    return response.data;
  },

  getActivos: async (): Promise<Usuario[]> => {
    const response = await apiClient.get<Usuario[]>('/usuarios/activos');
    return response.data;
  },

  create: async (data: CreateUsuarioRequest): Promise<Usuario> => {
    const response = await apiClient.post<Usuario>('/usuarios', data);
    return response.data;
  },

  update: async (id: number, data: UpdateUsuarioRequest): Promise<Usuario> => {
    const response = await apiClient.put<Usuario>(`/usuarios/${id}`, data);
    return response.data;
  },

  desactivar: async (id: number): Promise<Usuario> => {
    const response = await apiClient.patch<Usuario>(`/usuarios/${id}/desactivar`);
    return response.data;
  },

  activar: async (id: number): Promise<Usuario> => {
    const response = await apiClient.patch<Usuario>(`/usuarios/${id}/activar`);
    return response.data;
  },

  bloquear: async (id: number): Promise<Usuario> => {
    const response = await apiClient.patch<Usuario>(`/usuarios/${id}/bloquear`);
    return response.data;
  },

  desbloquear: async (id: number): Promise<Usuario> => {
    const response = await apiClient.patch<Usuario>(`/usuarios/${id}/desbloquear`);
    return response.data;
  },

  agregarRole: async (id: number, role: string): Promise<Usuario> => {
    const response = await apiClient.patch<Usuario>(`/usuarios/${id}/roles/agregar`, null, {
      params: { role },
    });
    return response.data;
  },

  removerRole: async (id: number, role: string): Promise<Usuario> => {
    const response = await apiClient.patch<Usuario>(`/usuarios/${id}/roles/remover`, null, {
      params: { role },
    });
    return response.data;
  },

  resetPassword: async (id: number, newPassword: string): Promise<Usuario> => {
    const response = await apiClient.patch<Usuario>(`/usuarios/${id}/reset-password`, {
      newPassword,
    });
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/usuarios/${id}`);
  },
};
