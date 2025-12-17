import apiClient from './axios-config';
import { Pedido } from '@/types/pedido.types';
import { PaginatedResponse, QueryParams } from '@/types/api.types';

export const pedidosApi = {
  getAll: async (params?: QueryParams): Promise<PaginatedResponse<Pedido>> => {
    const response = await apiClient.get<PaginatedResponse<Pedido>>('/pedidos', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Pedido> => {
    const response = await apiClient.get<Pedido>(`/pedidos/${id}`);
    return response.data;
  },

  getByTracking: async (codigo: string): Promise<Pedido> => {
    const response = await apiClient.get<Pedido>(`/pedidos/tracking/${codigo}`);
    return response.data;
  },

  getByCliente: async (clienteId: number): Promise<Pedido[]> => {
    const response = await apiClient.get<Pedido[]>(`/pedidos/cliente/${clienteId}`);
    return response.data;
  },

  getByEstado: async (estado: string): Promise<Pedido[]> => {
    const response = await apiClient.get<Pedido[]>(`/pedidos/estado/${estado}`);
    return response.data;
  },

  create: async (data: Partial<Pedido>): Promise<Pedido> => {
    const response = await apiClient.post<Pedido>('/pedidos', data);
    return response.data;
  },

  update: async (id: number, data: Partial<Pedido>): Promise<Pedido> => {
    const response = await apiClient.put<Pedido>(`/pedidos/${id}`, data);
    return response.data;
  },

  updateEstado: async (id: number, estado: string, comentario?: string): Promise<Pedido> => {
    const response = await apiClient.patch<Pedido>(`/pedidos/${id}/estado`, null, {
      params: { estado, comentario },
    });
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/pedidos/${id}`);
  },
};
