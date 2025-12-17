import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { containersApi } from '@/api/containers.api';
import { CreateContenedorRequest, UpdateContenedorRequest } from '@/types/contenedor.types';
import { QueryParams } from '@/types/api.types';
import { message } from 'antd';

export const useContainers = (params?: QueryParams) => {
  const queryClient = useQueryClient();

  const { data, isLoading, error } = useQuery({
    queryKey: ['containers', params],
    queryFn: () => containersApi.getAll(params),
  });

  const createMutation = useMutation({
    mutationFn: (data: CreateContenedorRequest) => containersApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['containers'] });
      message.success('Container creado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al crear container';
      message.error(errorMsg);
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateContenedorRequest }) =>
      containersApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['containers'] });
      message.success('Container actualizado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al actualizar container';
      message.error(errorMsg);
    },
  });

  const consolidarMutation = useMutation({
    mutationFn: ({ id, productosIds }: { id: number; productosIds: number[] }) =>
      containersApi.consolidar(id, productosIds),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['containers'] });
      message.success('Productos consolidados exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al consolidar productos';
      message.error(errorMsg);
    },
  });

  const desconsolidarMutation = useMutation({
    mutationFn: (id: number) => containersApi.desconsolidar(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['containers'] });
      message.success('Container desconsolidado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al desconsolidar container';
      message.error(errorMsg);
    },
  });

  return {
    containers: (data as any)?.content || [],
    pagination: {
      current: ((data as any)?.number || 0) + 1,
      pageSize: (data as any)?.size || 20,
      total: (data as any)?.totalElements || 0,
      totalPages: (data as any)?.totalPages || 0,
    },
    isLoading,
    error,
    createContainer: createMutation.mutate,
    updateContainer: updateMutation.mutate,
    consolidarProductos: consolidarMutation.mutate,
    desconsolidarContainer: desconsolidarMutation.mutate,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
  };
};

export const useContainer = (id: number) => {
  return useQuery({
    queryKey: ['container', id],
    queryFn: () => containersApi.getById(id),
    enabled: !!id,
  });
};
