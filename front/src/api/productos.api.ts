import apiClient from './axios-config';
import { Producto, CreateProductoRequest, UpdateProductoRequest } from '@/types/producto.types';
import { PaginatedResponse, QueryParams } from '@/types/api.types';

export const productosApi = {
  getAll: async (params?: QueryParams): Promise<PaginatedResponse<Producto>> => {
    const response = await apiClient.get<PaginatedResponse<Producto>>('/productos', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Producto> => {
    const response = await apiClient.get<Producto>(`/productos/${id}`);
    return response.data;
  },

  getByCodigo: async (codigo: string): Promise<Producto> => {
    const response = await apiClient.get<Producto>(`/productos/codigo/${codigo}`);
    return response.data;
  },

  getByNcm: async (ncm: string, params?: QueryParams): Promise<PaginatedResponse<Producto>> => {
    const response = await apiClient.get<PaginatedResponse<Producto>>(`/productos/ncm/${ncm}`, { params });
    return response.data;
  },

  getPeligrosos: async (params?: QueryParams): Promise<PaginatedResponse<Producto>> => {
    const response = await apiClient.get<PaginatedResponse<Producto>>('/productos/peligrosos', { params });
    return response.data;
  },

  getRefrigerados: async (params?: QueryParams): Promise<PaginatedResponse<Producto>> => {
    const response = await apiClient.get<PaginatedResponse<Producto>>('/productos/refrigeracion', { params });
    return response.data;
  },

  search: async (nombre: string, params?: QueryParams): Promise<PaginatedResponse<Producto>> => {
    const response = await apiClient.get<PaginatedResponse<Producto>>('/productos/search', {
      params: { nombre, ...params },
    });
    return response.data;
  },

  create: async (data: CreateProductoRequest): Promise<Producto> => {
    const response = await apiClient.post<Producto>('/productos', data);
    return response.data;
  },

  update: async (id: number, data: UpdateProductoRequest): Promise<Producto> => {
    const response = await apiClient.put<Producto>(`/productos/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/productos/${id}`);
  },
};
