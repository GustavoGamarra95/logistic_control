import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { pedidosApi } from '@/api/pedidos.api';
import { Pedido } from '@/types/pedido.types';
import { QueryParams } from '@/types/api.types';
import { message } from 'antd';

export const usePedidos = (params?: QueryParams) => {
  const queryClient = useQueryClient();

  const { data, isLoading, error } = useQuery({
    queryKey: ['pedidos', params],
    queryFn: () => pedidosApi.getAll(params),
  });

  const createMutation = useMutation({
    mutationFn: (data: Partial<Pedido>) => pedidosApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pedidos'] });
      message.success('Pedido creado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al crear pedido';
      message.error(errorMsg);
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<Pedido> }) =>
      pedidosApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pedidos'] });
      message.success('Pedido actualizado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al actualizar pedido';
      message.error(errorMsg);
    },
  });

  const updateEstadoMutation = useMutation({
    mutationFn: ({ id, estado, comentario }: { id: number; estado: string; comentario?: string }) =>
      pedidosApi.updateEstado(id, estado, comentario),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pedidos'] });
      message.success('Estado actualizado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al actualizar estado';
      message.error(errorMsg);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => pedidosApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pedidos'] });
      message.success('Pedido eliminado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al eliminar pedido';
      message.error(errorMsg);
    },
  });

  return {
    pedidos: (data as any)?.content || [],
    pagination: {
      current: ((data as any)?.number || 0) + 1,
      pageSize: (data as any)?.size || 20,
      total: (data as any)?.totalElements || 0,
      totalPages: (data as any)?.totalPages || 0,
    },
    isLoading,
    error,
    createPedido: createMutation.mutate,
    updatePedido: updateMutation.mutate,
    updateEstado: updateEstadoMutation.mutate,
    deletePedido: deleteMutation.mutate,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
};

export const usePedido = (id: number) => {
  return useQuery({
    queryKey: ['pedido', id],
    queryFn: () => pedidosApi.getById(id),
    enabled: !!id,
  });
};
