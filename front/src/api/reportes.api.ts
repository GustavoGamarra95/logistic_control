import apiClient from './axios-config';

export interface ReportParams {
  reportId: string;
  format: 'PDF' | 'EXCEL';
  fechaDesde?: string;
  fechaHasta?: string;
  clienteId?: number;
  proveedorId?: number;
  estado?: string;
}

export const reportesApi = {
  /**
   * Genera un reporte en el formato especificado
   * Retorna un Blob para descargar el archivo
   */
  generate: async (params: ReportParams): Promise<Blob> => {
    const response = await apiClient.post<Blob>(
      '/reportes/generate',
      params,
      {
        responseType: 'blob',
      }
    );
    return response.data;
  },

  /**
   * Obtiene vista previa del reporte (URL del PDF)
   */
  preview: async (params: Omit<ReportParams, 'format'>): Promise<string> => {
    const response = await apiClient.post<{ url: string }>(
      '/reportes/preview',
      { ...params, format: 'PDF' }
    );
    return response.data.url;
  },

  /**
   * Lista de reportes disponibles
   */
  getAvailableReports: async (): Promise<any[]> => {
    const response = await apiClient.get('/reportes/available');
    return response.data;
  },

  /**
   * Descarga reporte de pedidos por estado
   */
  pedidosPorEstado: async (fechaDesde: string, fechaHasta: string, format: 'PDF' | 'EXCEL' = 'PDF'): Promise<Blob> => {
    return reportesApi.generate({
      reportId: 'pedidos-por-estado',
      format,
      fechaDesde,
      fechaHasta,
    });
  },

  /**
   * Descarga reporte de facturas emitidas
   */
  facturasEmitidas: async (fechaDesde: string, fechaHasta: string, clienteId?: number, format: 'PDF' | 'EXCEL' = 'PDF'): Promise<Blob> => {
    return reportesApi.generate({
      reportId: 'facturas-emitidas',
      format,
      fechaDesde,
      fechaHasta,
      clienteId,
    });
  },

  /**
   * Descarga reporte de inventario valorizado
   */
  inventarioValorizado: async (format: 'PDF' | 'EXCEL' = 'PDF'): Promise<Blob> => {
    return reportesApi.generate({
      reportId: 'inventario-valorizado',
      format,
    });
  },

  /**
   * Descarga reporte financiero
   */
  ingresosPorPeriodo: async (fechaDesde: string, fechaHasta: string, format: 'PDF' | 'EXCEL' = 'PDF'): Promise<Blob> => {
    return reportesApi.generate({
      reportId: 'ingresos-por-periodo',
      format,
      fechaDesde,
      fechaHasta,
    });
  },
};
