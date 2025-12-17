import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { usuariosApi } from '@/api/usuarios.api';
import { CreateUsuarioRequest, UpdateUsuarioRequest } from '@/types/usuario.types';
import { QueryParams } from '@/types/api.types';
import { message } from 'antd';

export const useUsuarios = (params?: QueryParams) => {
  const queryClient = useQueryClient();

  const { data, isLoading, error } = useQuery({
    queryKey: ['usuarios', params],
    queryFn: () => usuariosApi.getAll(params),
  });

  const createMutation = useMutation({
    mutationFn: (data: CreateUsuarioRequest) => usuariosApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['usuarios'] });
      message.success('Usuario creado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al crear usuario';
      message.error(errorMsg);
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateUsuarioRequest }) =>
      usuariosApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['usuarios'] });
      message.success('Usuario actualizado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al actualizar usuario';
      message.error(errorMsg);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => usuariosApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['usuarios'] });
      message.success('Usuario eliminado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al eliminar usuario';
      message.error(errorMsg);
    },
  });

  const activarMutation = useMutation({
    mutationFn: (id: number) => usuariosApi.activar(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['usuarios'] });
      message.success('Usuario activado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al activar usuario';
      message.error(errorMsg);
    },
  });

  const desactivarMutation = useMutation({
    mutationFn: (id: number) => usuariosApi.desactivar(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['usuarios'] });
      message.success('Usuario desactivado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al desactivar usuario';
      message.error(errorMsg);
    },
  });

  const bloquearMutation = useMutation({
    mutationFn: (id: number) => usuariosApi.bloquear(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['usuarios'] });
      message.success('Usuario bloqueado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al bloquear usuario';
      message.error(errorMsg);
    },
  });

  const desbloquearMutation = useMutation({
    mutationFn: (id: number) => usuariosApi.desbloquear(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['usuarios'] });
      message.success('Usuario desbloqueado exitosamente');
    },
    onError: (error: any) => {
      const errorMsg = error.response?.data?.message || 'Error al desbloquear usuario';
      message.error(errorMsg);
    },
  });

  return {
    usuarios: (data as any)?.content || [],
    pagination: {
      current: ((data as any)?.number || 0) + 1,
      pageSize: (data as any)?.size || 20,
      total: (data as any)?.totalElements || 0,
      totalPages: (data as any)?.totalPages || 0,
    },
    isLoading,
    error,
    createUsuario: createMutation.mutate,
    updateUsuario: updateMutation.mutate,
    deleteUsuario: deleteMutation.mutate,
    activarUsuario: activarMutation.mutate,
    desactivarUsuario: desactivarMutation.mutate,
    bloquearUsuario: bloquearMutation.mutate,
    desbloquearUsuario: desbloquearMutation.mutate,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isDeleting: deleteMutation.isPending,
  };
};

export const useUsuario = (id: number) => {
  return useQuery({
    queryKey: ['usuario', id],
    queryFn: () => usuariosApi.getById(id),
    enabled: !!id,
  });
};

export const useUsuariosActivos = () => {
  return useQuery({
    queryKey: ['usuarios', 'activos'],
    queryFn: () => usuariosApi.getActivos(),
  });
};
