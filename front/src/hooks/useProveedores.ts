import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { proveedoresApi } from '@/api/proveedores.api';
import { CreateProveedorRequest, UpdateProveedorRequest } from '@/types/proveedor.types';
import { QueryParams } from '@/types/api.types';
import { message } from 'antd';

export const useProveedores = (params?: QueryParams) => {
  const queryClient = useQueryClient();

  const { data, isLoading, error } = useQuery({
    queryKey: ['proveedores', params],
    queryFn: () => proveedoresApi.getAll(params),
  });

  const createMutation = useMutation({
    mutationFn: (data: CreateProveedorRequest) => proveedoresApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['proveedores'] });
      message.success('Proveedor creado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al crear proveedor';
      message.error(errorMsg);
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateProveedorRequest }) =>
      proveedoresApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['proveedores'] });
      message.success('Proveedor actualizado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al actualizar proveedor';
      message.error(errorMsg);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: ({ id, reason }: { id: number; reason: string }) =>
      proveedoresApi.delete(id, reason),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['proveedores'] });
      message.success('Proveedor eliminado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al eliminar proveedor';
      message.error(errorMsg);
    },
  });

  return {
    proveedores: (data as any)?.content || [],
    pagination: {
      current: ((data as any)?.number || 0) + 1,
      pageSize: (data as any)?.size || 20,
      total: (data as any)?.totalElements || 0,
      totalPages: (data as any)?.totalPages || 0,
    },
    isLoading,
    error,
    createProveedor: createMutation.mutate,
    updateProveedor: updateMutation.mutate,
    deleteProveedor: deleteMutation.mutate,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
};

export const useProveedor = (id: number) => {
  return useQuery({
    queryKey: ['proveedor', id],
    queryFn: () => proveedoresApi.getById(id),
    enabled: !!id,
  });
};
