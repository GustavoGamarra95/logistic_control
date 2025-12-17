import apiClient from './axios-config';
import { Cliente, CreateClienteRequest, UpdateClienteRequest } from '@/types/cliente.types';
import { PaginatedResponse, QueryParams } from '@/types/api.types';

export const clientesApi = {
  getAll: async (params?: QueryParams): Promise<PaginatedResponse<Cliente>> => {
    const response = await apiClient.get<PaginatedResponse<Cliente>>('/clientes', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Cliente> => {
    const response = await apiClient.get<Cliente>(`/clientes/${id}`);
    return response.data;
  },

  getByRuc: async (ruc: string): Promise<Cliente> => {
    const response = await apiClient.get<Cliente>(`/clientes/ruc/${ruc}`);
    return response.data;
  },

  create: async (data: CreateClienteRequest): Promise<Cliente> => {
    const response = await apiClient.post<Cliente>('/clientes', data);
    return response.data;
  },

  update: async (id: number, data: UpdateClienteRequest): Promise<Cliente> => {
    const response = await apiClient.put<Cliente>(`/clientes/${id}`, data);
    return response.data;
  },

  updateCredito: async (id: number, monto: number): Promise<Cliente> => {
    const response = await apiClient.patch<Cliente>(`/clientes/${id}/credito`, null, {
      params: { monto },
    });
    return response.data;
  },

  verificarRuc: async (id: number): Promise<any> => {
    const response = await apiClient.post(`/clientes/${id}/verificar-ruc`);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/clientes/${id}`);
  },
};
