import apiClient from './axios-config';
import { Contenedor, CreateContenedorRequest, UpdateContenedorRequest } from '@/types/contenedor.types';
import { PaginatedResponse, QueryParams } from '@/types/api.types';

export const containersApi = {
  getAll: async (params?: QueryParams): Promise<PaginatedResponse<Contenedor>> => {
    const response = await apiClient.get<PaginatedResponse<Contenedor>>('/containers', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Contenedor> => {
    const response = await apiClient.get<Contenedor>(`/containers/${id}`);
    return response.data;
  },

  getByNumero: async (numero: string): Promise<Contenedor> => {
    const response = await apiClient.get<Contenedor>(`/containers/numero/${numero}`);
    return response.data;
  },

  create: async (data: CreateContenedorRequest): Promise<Contenedor> => {
    const response = await apiClient.post<Contenedor>('/containers', data);
    return response.data;
  },

  update: async (id: number, data: UpdateContenedorRequest): Promise<Contenedor> => {
    const response = await apiClient.put<Contenedor>(`/containers/${id}`, data);
    return response.data;
  },

  consolidar: async (id: number, productosIds: number[]): Promise<Contenedor> => {
    const response = await apiClient.post<Contenedor>(`/containers/${id}/consolidar`, productosIds);
    return response.data;
  },

  desconsolidar: async (id: number): Promise<Contenedor> => {
    const response = await apiClient.post<Contenedor>(`/containers/${id}/desconsolidar`);
    return response.data;
  },
};
