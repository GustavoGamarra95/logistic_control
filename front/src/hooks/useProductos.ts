import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { productosApi } from '@/api/productos.api';
import { CreateProductoRequest, UpdateProductoRequest } from '@/types/producto.types';
import { QueryParams } from '@/types/api.types';
import { message } from 'antd';

export const useProductos = (params?: QueryParams) => {
  const queryClient = useQueryClient();

  const { data, isLoading, error } = useQuery({
    queryKey: ['productos', params],
    queryFn: () => productosApi.getAll(params),
  });

  const createMutation = useMutation({
    mutationFn: (data: CreateProductoRequest) => productosApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['productos'] });
      message.success('Producto creado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al crear producto';
      message.error(errorMsg);
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateProductoRequest }) =>
      productosApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['productos'] });
      message.success('Producto actualizado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al actualizar producto';
      message.error(errorMsg);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => productosApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['productos'] });
      message.success('Producto eliminado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al eliminar producto';
      message.error(errorMsg);
    },
  });

  return {
    productos: (data as any)?.content || [],
    pagination: {
      current: ((data as any)?.number || 0) + 1,
      pageSize: (data as any)?.size || 20,
      total: (data as any)?.totalElements || 0,
      totalPages: (data as any)?.totalPages || 0,
    },
    isLoading,
    error,
    createProducto: createMutation.mutate,
    updateProducto: updateMutation.mutate,
    deleteProducto: deleteMutation.mutate,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
};

export const useProducto = (id: number) => {
  return useQuery({
    queryKey: ['producto', id],
    queryFn: () => productosApi.getById(id),
    enabled: !!id,
  });
};

export const useProductosPeligrosos = (params?: QueryParams) => {
  return useQuery({
    queryKey: ['productos', 'peligrosos', params],
    queryFn: () => productosApi.getPeligrosos(params),
  });
};

export const useProductosRefrigerados = (params?: QueryParams) => {
  return useQuery({
    queryKey: ['productos', 'refrigerados', params],
    queryFn: () => productosApi.getRefrigerados(params),
  });
};
