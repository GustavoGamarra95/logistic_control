import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { inventarioApi } from '@/api/inventario.api';
import { CreateInventarioRequest, UpdateInventarioRequest } from '@/types/inventario.types';
import { QueryParams } from '@/types/api.types';
import { message } from 'antd';

export const useInventario = (params?: QueryParams) => {
  const queryClient = useQueryClient();

  const { data, isLoading, error } = useQuery({
    queryKey: ['inventario', params],
    queryFn: () => inventarioApi.getAll(params),
  });

  const createMutation = useMutation({
    mutationFn: (data: CreateInventarioRequest) => inventarioApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['inventario'] });
      message.success('Inventario creado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al crear inventario';
      message.error(errorMsg);
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateInventarioRequest }) =>
      inventarioApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['inventario'] });
      message.success('Inventario actualizado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al actualizar inventario';
      message.error(errorMsg);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => inventarioApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['inventario'] });
      message.success('Inventario eliminado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al eliminar inventario';
      message.error(errorMsg);
    },
  });

  const registrarEntradaMutation = useMutation({
    mutationFn: (id: number) => inventarioApi.registrarEntrada(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['inventario'] });
      message.success('Entrada registrada exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al registrar entrada';
      message.error(errorMsg);
    },
  });

  const registrarSalidaMutation = useMutation({
    mutationFn: ({ id, cantidad }: { id: number; cantidad: number }) =>
      inventarioApi.registrarSalida(id, cantidad),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['inventario'] });
      message.success('Salida registrada exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al registrar salida';
      message.error(errorMsg);
    },
  });

  const reservarMutation = useMutation({
    mutationFn: ({ id, cantidad }: { id: number; cantidad: number }) =>
      inventarioApi.reservar(id, cantidad),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['inventario'] });
      message.success('Inventario reservado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al reservar inventario';
      message.error(errorMsg);
    },
  });

  const liberarReservaMutation = useMutation({
    mutationFn: ({ id, cantidad }: { id: number; cantidad: number }) =>
      inventarioApi.liberarReserva(id, cantidad),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['inventario'] });
      message.success('Reserva liberada exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al liberar reserva';
      message.error(errorMsg);
    },
  });

  return {
    inventario: (data as any)?.content || [],
    pagination: {
      current: ((data as any)?.number || 0) + 1,
      pageSize: (data as any)?.size || 20,
      total: (data as any)?.totalElements || 0,
      totalPages: (data as any)?.totalPages || 0,
    },
    isLoading,
    error,
    createInventario: createMutation.mutate,
    updateInventario: updateMutation.mutate,
    deleteInventario: deleteMutation.mutate,
    registrarEntrada: registrarEntradaMutation.mutate,
    registrarSalida: registrarSalidaMutation.mutate,
    reservar: reservarMutation.mutate,
    liberarReserva: liberarReservaMutation.mutate,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
};

export const useInventarioDisponible = () => {
  return useQuery({
    queryKey: ['inventario', 'disponible'],
    queryFn: () => inventarioApi.getDisponible(),
  });
};
