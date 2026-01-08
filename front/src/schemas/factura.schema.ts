import { z } from 'zod';

export const itemFacturaSchema = z.object({
  codigo: z.string().max(50).optional(),
  descripcion: z.string().min(3, 'La descripción es requerida').max(500),
  cantidad: z.number().min(1, 'La cantidad debe ser mayor a 0'),
  unidadMedida: z.string().min(1, 'Unidad de medida requerida').max(20),
  precioUnitario: z.number().min(0, 'El precio debe ser mayor o igual a 0'),
  tasaIva: z.number().min(0).max(100, 'Tasa de IVA inválida'),
});

export const facturaSchema = z.object({
  clienteId: z.number().min(1, 'Debe seleccionar un cliente'),

  pedidoId: z.number().optional(),

  tipo: z.enum(['CONTADO', 'CREDITO'], {
    errorMap: () => ({ message: 'Tipo de factura inválido' }),
  }),

  fechaEmision: z.string().optional(),

  fechaVencimiento: z.string().optional(),

  condicionPago: z.string().max(200).optional(),

  moneda: z.string().min(3).max(3, 'Código de moneda debe ser de 3 caracteres'),

  observaciones: z.string().max(1000).optional(),

  items: z.array(itemFacturaSchema).min(1, 'Debe agregar al menos un ítem'),
});

export type FacturaFormData = z.infer<typeof facturaSchema>;
export type ItemFacturaFormData = z.infer<typeof itemFacturaSchema>;
