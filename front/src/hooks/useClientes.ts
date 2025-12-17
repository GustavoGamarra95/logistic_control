import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { clientesApi } from '@/api/clientes.api';
import { CreateClienteRequest, UpdateClienteRequest } from '@/types/cliente.types';
import { QueryParams } from '@/types/api.types';
import { message } from 'antd';

export const useClientes = (params?: QueryParams) => {
  const queryClient = useQueryClient();

  const { data, isLoading, error } = useQuery({
    queryKey: ['clientes', params],
    queryFn: () => clientesApi.getAll(params),
  });

  const createMutation = useMutation({
    mutationFn: (data: CreateClienteRequest) => clientesApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clientes'] });
      message.success('Cliente creado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al crear cliente';
      message.error(errorMsg);
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateClienteRequest }) =>
      clientesApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clientes'] });
      message.success('Cliente actualizado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al actualizar cliente';
      message.error(errorMsg);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => clientesApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clientes'] });
      message.success('Cliente eliminado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al eliminar cliente';
      message.error(errorMsg);
    },
  });

  const updateCreditoMutation = useMutation({
    mutationFn: ({ id, monto }: { id: number; monto: number }) =>
      clientesApi.updateCredito(id, monto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clientes'] });
      message.success('Crédito actualizado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al actualizar crédito';
      message.error(errorMsg);
    },
  });

  return {
    clientes: (data as any)?.content || [],
    pagination: {
      current: ((data as any)?.number || 0) + 1,
      pageSize: (data as any)?.size || 20,
      total: (data as any)?.totalElements || 0,
      totalPages: (data as any)?.totalPages || 0,
    },
    isLoading,
    error,
    createCliente: createMutation.mutate,
    updateCliente: updateMutation.mutate,
    deleteCliente: deleteMutation.mutate,
    updateCredito: updateCreditoMutation.mutate,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
};

export const useCliente = (id: number) => {
  return useQuery({
    queryKey: ['cliente', id],
    queryFn: () => clientesApi.getById(id),
    enabled: !!id,
  });
};
