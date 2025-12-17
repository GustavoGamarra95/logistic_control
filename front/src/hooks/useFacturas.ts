import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { facturasApi } from '@/api/facturas.api';
import { CreateFacturaRequest, UpdateFacturaRequest } from '@/types/factura.types';
import { QueryParams } from '@/types/api.types';
import { message } from 'antd';

export const useFacturas = (params?: QueryParams) => {
  const queryClient = useQueryClient();

  const { data, isLoading, error } = useQuery({
    queryKey: ['facturas', params],
    queryFn: () => facturasApi.getAll(params),
  });

  const createMutation = useMutation({
    mutationFn: (data: CreateFacturaRequest) => facturasApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturas'] });
      message.success('Factura creada exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al crear factura';
      message.error(errorMsg);
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateFacturaRequest }) =>
      facturasApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturas'] });
      message.success('Factura actualizada exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al actualizar factura';
      message.error(errorMsg);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => facturasApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturas'] });
      message.success('Factura eliminada exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al eliminar factura';
      message.error(errorMsg);
    },
  });

  const anularMutation = useMutation({
    mutationFn: (id: number) => facturasApi.anular(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturas'] });
      message.success('Factura anulada exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al anular factura';
      message.error(errorMsg);
    },
  });

  const enviarSifenMutation = useMutation({
    mutationFn: ({ id, request }: { id: number; request?: any }) =>
      facturasApi.enviarASifen(id, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturas'] });
      message.success('Factura enviada a SIFEN exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al enviar factura a SIFEN';
      message.error(errorMsg);
    },
  });

  const actualizarEstadoSifenMutation = useMutation({
    mutationFn: (id: number) => facturasApi.actualizarEstadoSifen(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturas'] });
      message.success('Estado actualizado desde SIFEN');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al actualizar estado desde SIFEN';
      message.error(errorMsg);
    },
  });

  return {
    facturas: (data as any)?.content || [],
    pagination: {
      current: ((data as any)?.number || 0) + 1,
      pageSize: (data as any)?.size || 20,
      total: (data as any)?.totalElements || 0,
      totalPages: (data as any)?.totalPages || 0,
    },
    isLoading,
    error,
    createFactura: createMutation.mutate,
    updateFactura: updateMutation.mutate,
    deleteFactura: deleteMutation.mutate,
    anularFactura: anularMutation.mutate,
    enviarASifen: enviarSifenMutation.mutate,
    actualizarEstadoSifen: actualizarEstadoSifenMutation.mutate,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
};

export const useFactura = (id: number) => {
  return useQuery({
    queryKey: ['factura', id],
    queryFn: () => facturasApi.getById(id),
    enabled: !!id,
  });
};

export const useFacturasPendientes = () => {
  return useQuery({
    queryKey: ['facturas', 'pendientes'],
    queryFn: () => facturasApi.getPendientes(),
  });
};

export const useFacturasVencidas = () => {
  return useQuery({
    queryKey: ['facturas', 'vencidas'],
    queryFn: () => facturasApi.getVencidas(),
  });
};
