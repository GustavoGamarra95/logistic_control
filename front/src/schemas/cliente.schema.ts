import { z } from 'zod';

// Validación de RUC paraguayo (6-8 dígitos + guion + 1 dígito verificador)
const rucRegex = /^\d{6,8}$/;
const dvRegex = /^\d{1}$/;

export const clienteSchema = z.object({
  razonSocial: z
    .string()
    .min(3, 'La razón social debe tener al menos 3 caracteres')
    .max(200, 'La razón social no puede exceder 200 caracteres'),
  
  nombreFantasia: z
    .string()
    .max(200, 'El nombre fantasía no puede exceder 200 caracteres')
    .optional(),
  
  ruc: z
    .string()
    .regex(rucRegex, 'El RUC debe tener entre 6 y 8 dígitos'),
  
  dv: z
    .string()
    .regex(dvRegex, 'El dígito verificador debe ser un solo dígito'),
  
  direccion: z
    .string()
    .min(5, 'La dirección debe tener al menos 5 caracteres')
    .max(300, 'La dirección no puede exceder 300 caracteres'),
  
  ciudad: z
    .string()
    .min(2, 'La ciudad debe tener al menos 2 caracteres')
    .max(100, 'La ciudad no puede exceder 100 caracteres'),
  
  pais: z
    .string()
    .min(2, 'El país debe tener al menos 2 caracteres')
    .max(100, 'El país no puede exceder 100 caracteres'),
  
  contacto: z
    .string()
    .max(200, 'El nombre de contacto no puede exceder 200 caracteres')
    .optional(),
  
  email: z
    .string()
    .email('Debe ingresar un email válido')
    .max(255, 'El email no puede exceder 255 caracteres'),
  
  telefono: z
    .string()
    .max(50, 'El teléfono no puede exceder 50 caracteres')
    .optional(),
  
  celular: z
    .string()
    .max(50, 'El celular no puede exceder 50 caracteres')
    .optional(),
  
  tipoServicio: z.enum(['AEREO', 'MARITIMO', 'TERRESTRE', 'MULTIMODAL'], {
    errorMap: () => ({ message: 'Debe seleccionar un tipo de servicio válido' }),
  }),
  
  creditoLimite: z
    .number()
    .min(0, 'El límite de crédito no puede ser negativo')
    .max(999999999999, 'El límite de crédito excede el máximo permitido'),
  
  esFacturadorElectronico: z.boolean(),
  
  observaciones: z
    .string()
    .max(1000, 'Las observaciones no pueden exceder 1000 caracteres')
    .optional(),
});

export type ClienteFormData = z.infer<typeof clienteSchema>;
