import apiClient from './axios-config';
import { Proveedor, CreateProveedorRequest, UpdateProveedorRequest, TipoProveedor } from '@/types/proveedor.types';
import { PaginatedResponse, QueryParams } from '@/types/api.types';

export const proveedoresApi = {
  getAll: async (params?: QueryParams): Promise<PaginatedResponse<Proveedor>> => {
    const response = await apiClient.get<PaginatedResponse<Proveedor>>('/proveedores', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Proveedor> => {
    const response = await apiClient.get<Proveedor>(`/proveedores/${id}`);
    return response.data;
  },

  getByRuc: async (ruc: string): Promise<Proveedor> => {
    const response = await apiClient.get<Proveedor>(`/proveedores/ruc/${ruc}`);
    return response.data;
  },

  getByTipo: async (tipo: TipoProveedor): Promise<Proveedor[]> => {
    const response = await apiClient.get<Proveedor[]>(`/proveedores/tipo/${tipo}`);
    return response.data;
  },

  create: async (data: CreateProveedorRequest): Promise<Proveedor> => {
    const response = await apiClient.post<Proveedor>('/proveedores', data);
    return response.data;
  },

  update: async (id: number, data: UpdateProveedorRequest): Promise<Proveedor> => {
    const response = await apiClient.put<Proveedor>(`/proveedores/${id}`, data);
    return response.data;
  },

  delete: async (id: number, reason?: string): Promise<void> => {
    await apiClient.delete(`/proveedores/${id}`, {
      data: reason ? { reason } : undefined
    });
  },
};
