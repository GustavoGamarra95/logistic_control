import { z } from 'zod';

export const pedidoSchema = z.object({
  clienteId: z
    .number()
    .min(1, 'Debe seleccionar un cliente'),
  
  paisOrigen: z
    .string()
    .min(2, 'El país de origen es requerido'),
  
  paisDestino: z
    .string()
    .min(2, 'El país de destino es requerido'),
  
  ciudadOrigen: z
    .string()
    .max(100)
    .optional(),
  
  ciudadDestino: z
    .string()
    .max(100)
    .optional(),
  
  tipoCarga: z.enum([
    'FCL',
    'LCL',
    'GRANEL',
    'PERECEDERO',
    'PELIGROSO',
    'FRAGIL'
  ], {
    errorMap: () => ({ message: 'Debe seleccionar un tipo de carga válido' }),
  }),
  
  descripcionMercaderia: z
    .string()
    .min(10, 'La descripción debe tener al menos 10 caracteres')
    .max(500, 'La descripción no puede exceder 500 caracteres'),
  
  fechaEstimadaLlegada: z
    .string()
    .optional(),
  
  pesoTotalKg: z
    .number()
    .min(0.01, 'El peso debe ser mayor a 0')
    .max(999999, 'El peso excede el máximo permitido'),
  
  volumenTotalM3: z
    .number()
    .min(0.01, 'El volumen debe ser mayor a 0')
    .max(999999, 'El volumen excede el máximo permitido'),
  
  valorDeclarado: z
    .number()
    .min(0, 'El valor declarado no puede ser negativo'),
  
  moneda: z
    .string()
    .min(3, 'Debe seleccionar una moneda')
    .max(3, 'Código de moneda inválido'),
  
  numeroBlAwb: z
    .string()
    .max(100)
    .optional(),
  
  puertoEmbarque: z
    .string()
    .max(200)
    .optional(),
  
  puertoDestino: z
    .string()
    .max(200)
    .optional(),
  
  empresaTransporte: z
    .string()
    .max(200)
    .optional(),
  
  requiereSeguro: z.boolean(),
  
  valorSeguro: z
    .number()
    .min(0)
    .optional(),
  
  direccionEntrega: z
    .string()
    .max(500)
    .optional(),
  
  formaPago: z
    .string()
    .max(100)
    .optional(),
  
  observaciones: z
    .string()
    .max(1000)
    .optional(),
});

export type PedidoFormData = z.infer<typeof pedidoSchema>;
