import apiClient from './axios-config';
import { Factura, CreateFacturaRequest, UpdateFacturaRequest, EstadoFactura } from '@/types/factura.types';
import { PaginatedResponse, QueryParams } from '@/types/api.types';

export const facturasApi = {
  getAll: async (params?: QueryParams): Promise<PaginatedResponse<Factura>> => {
    const response = await apiClient.get<PaginatedResponse<Factura>>('/facturas', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Factura> => {
    const response = await apiClient.get<Factura>(`/facturas/${id}`);
    return response.data;
  },

  getByNumero: async (numero: string): Promise<Factura> => {
    const response = await apiClient.get<Factura>(`/facturas/numero/${numero}`);
    return response.data;
  },

  getByCdc: async (cdc: string): Promise<Factura> => {
    const response = await apiClient.get<Factura>(`/facturas/cdc/${cdc}`);
    return response.data;
  },

  getByCliente: async (clienteId: number): Promise<Factura[]> => {
    const response = await apiClient.get<Factura[]>(`/facturas/cliente/${clienteId}`);
    return response.data;
  },

  getByEstado: async (estado: EstadoFactura): Promise<Factura[]> => {
    const response = await apiClient.get<Factura[]>(`/facturas/estado/${estado}`);
    return response.data;
  },

  getPendientes: async (): Promise<Factura[]> => {
    const response = await apiClient.get<Factura[]>('/facturas/pendientes');
    return response.data;
  },

  getVencidas: async (): Promise<Factura[]> => {
    const response = await apiClient.get<Factura[]>('/facturas/vencidas');
    return response.data;
  },

  getByRangoFecha: async (desde: string, hasta: string): Promise<Factura[]> => {
    const response = await apiClient.get<Factura[]>('/facturas/rango-fecha', {
      params: { desde, hasta },
    });
    return response.data;
  },

  create: async (data: CreateFacturaRequest): Promise<Factura> => {
    const response = await apiClient.post<Factura>('/facturas', data);
    return response.data;
  },

  update: async (id: number, data: UpdateFacturaRequest): Promise<Factura> => {
    const response = await apiClient.put<Factura>(`/facturas/${id}`, data);
    return response.data;
  },

  calcularTotales: async (id: number): Promise<Factura> => {
    const response = await apiClient.patch<Factura>(`/facturas/${id}/calcular-totales`);
    return response.data;
  },

  aprobarSifen: async (id: number, cdc: string, respuesta: string): Promise<Factura> => {
    const response = await apiClient.patch<Factura>(`/facturas/${id}/aprobar-sifen`, null, {
      params: { cdc, respuesta },
    });
    return response.data;
  },

  rechazarSifen: async (id: number, codigo: string, mensaje: string): Promise<Factura> => {
    const response = await apiClient.patch<Factura>(`/facturas/${id}/rechazar-sifen`, null, {
      params: { codigo, mensaje },
    });
    return response.data;
  },

  anular: async (id: number): Promise<Factura> => {
    const response = await apiClient.patch<Factura>(`/facturas/${id}/anular`);
    return response.data;
  },

  enviarASifen: async (id: number, request?: any): Promise<any> => {
    const response = await apiClient.post(`/facturas/${id}/enviar-sifen`, request);
    return response.data;
  },

  enviarLoteASifen: async (facturasIds: number[]): Promise<any> => {
    const response = await apiClient.post('/facturas/lote/enviar-sifen', facturasIds);
    return response.data;
  },

  consultarEstadoSifen: async (cdc: string): Promise<any> => {
    const response = await apiClient.get(`/facturas/consultar-sifen/${cdc}`);
    return response.data;
  },

  consultarLoteSifen: async (numeroLote: string): Promise<any> => {
    const response = await apiClient.get(`/facturas/lote/consultar-sifen/${numeroLote}`);
    return response.data;
  },

  actualizarEstadoSifen: async (id: number): Promise<Factura> => {
    const response = await apiClient.post<Factura>(`/facturas/${id}/actualizar-estado-sifen`);
    return response.data;
  },

  getXml: async (id: number): Promise<string> => {
    const response = await apiClient.get<string>(`/facturas/${id}/xml`);
    return response.data;
  },

  getXmlFirmado: async (id: number): Promise<string> => {
    const response = await apiClient.get<string>(`/facturas/${id}/xml-firmado`);
    return response.data;
  },

  getQr: async (id: number): Promise<string> => {
    const response = await apiClient.get<string>(`/facturas/${id}/qr`);
    return response.data;
  },

  getKudeUrl: async (id: number): Promise<string> => {
    const response = await apiClient.get<string>(`/facturas/${id}/kude`);
    return response.data;
  },

  regenerarXml: async (id: number): Promise<Factura> => {
    const response = await apiClient.post<Factura>(`/facturas/${id}/regenerar-xml`);
    return response.data;
  },

  validarFirma: async (id: number): Promise<boolean> => {
    const response = await apiClient.get<boolean>(`/facturas/${id}/validar-firma`);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.delete(`/facturas/${id}`);
  },
};
