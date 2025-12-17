import apiClient from './axios-config';
import { Inventario, CreateInventarioRequest, UpdateInventarioRequest, EstadoInventario } from '@/types/inventario.types';
import { PaginatedResponse, QueryParams } from '@/types/api.types';

export const inventarioApi = {
  getAll: async (params?: QueryParams): Promise<PaginatedResponse<Inventario>> => {
    const response = await apiClient.get<PaginatedResponse<Inventario>>('/inventario', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Inventario> => {
    const response = await apiClient.get<Inventario>(`/inventario/${id}`);
    return response.data;
  },

  getByCliente: async (clienteId: number): Promise<Inventario[]> => {
    const response = await apiClient.get<Inventario[]>(`/inventario/cliente/${clienteId}`);
    return response.data;
  },

  getByProducto: async (productoId: number): Promise<Inventario[]> => {
    const response = await apiClient.get<Inventario[]>(`/inventario/producto/${productoId}`);
    return response.data;
  },

  getByEstado: async (estado: EstadoInventario): Promise<Inventario[]> => {
    const response = await apiClient.get<Inventario[]>(`/inventario/estado/${estado}`);
    return response.data;
  },

  getDisponible: async (): Promise<Inventario[]> => {
    const response = await apiClient.get<Inventario[]>('/inventario/disponible');
    return response.data;
  },

  getByUbicacion: async (ubicacion: string): Promise<Inventario[]> => {
    const response = await apiClient.get<Inventario[]>(`/inventario/ubicacion/${ubicacion}`);
    return response.data;
  },

  create: async (data: CreateInventarioRequest): Promise<Inventario> => {
    const response = await apiClient.post<Inventario>('/inventario', data);
    return response.data;
  },

  update: async (id: number, data: UpdateInventarioRequest): Promise<Inventario> => {
    const response = await apiClient.put<Inventario>(`/inventario/${id}`, data);
    return response.data;
  },

  registrarEntrada: async (id: number): Promise<Inventario> => {
    const response = await apiClient.patch<Inventario>(`/inventario/${id}/entrada`);
    return response.data;
  },

  registrarSalida: async (id: number, cantidad: number): Promise<Inventario> => {
    const response = await apiClient.patch<Inventario>(`/inventario/${id}/salida`, null, {
      params: { cantidad },
    });
    return response.data;
  },

  reservar: async (id: number, cantidad: number): Promise<Inventario> => {
    const response = await apiClient.patch<Inventario>(`/inventario/${id}/reservar`, null, {
      params: { cantidad },
    });
    return response.data;
  },

  liberarReserva: async (id: number, cantidad: number): Promise<Inventario> => {
    const response = await apiClient.patch<Inventario>(`/inventario/${id}/liberar-reserva`, null, {
      params: { cantidad },
    });
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/inventario/${id}`);
  },
};
