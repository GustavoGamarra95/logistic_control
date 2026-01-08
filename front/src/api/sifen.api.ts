import apiClient from './axios-config';

export interface SifenConfig {
  ambiente: 'test' | 'prod';
  rucEmisor: string;
  razonSocialEmisor: string;
  nombreFantasia: string;
  timbrado: string;
  establecimiento: string;
  puntoExpedicion: string;
  actividadEconomica: string;
  direccion: string;
  telefono: string;
  email: string;
  ciudad: string;
  departamento: string;
  contingenciaEnabled: boolean;
  maxReintentos: number;
  connectTimeout: number;
  readTimeout: number;
}

export interface SifenEstadoResponse {
  conectado: boolean;
  ambiente: string;
  mensaje: string;
  timestamp: string;
}

export const sifenApi = {
  /**
   * Obtiene la configuración actual de SIFEN
   */
  getConfig: async (): Promise<SifenConfig> => {
    const response = await apiClient.get('/sifen/config');
    return response.data;
  },

  /**
   * Actualiza la configuración de SIFEN
   */
  updateConfig: async (config: Partial<SifenConfig>): Promise<SifenConfig> => {
    const response = await apiClient.put('/sifen/config', config);
    return response.data;
  },

  /**
   * Verifica el estado de conexión con SIFEN
   */
  verificarConexion: async (): Promise<SifenEstadoResponse> => {
    const response = await apiClient.get('/sifen/verificar-conexion');
    return response.data;
  },

  /**
   * Consulta el estado de una factura en SIFEN por CDC
   */
  consultarEstadoFactura: async (cdc: string) => {
    const response = await apiClient.get(`/facturas/consultar-sifen/${cdc}`);
    return response.data;
  },

  /**
   * Consulta el estado de un lote en SIFEN
   */
  consultarEstadoLote: async (numeroLote: string) => {
    const response = await apiClient.get(`/facturas/lote/consultar-sifen/${numeroLote}`);
    return response.data;
  },
};
