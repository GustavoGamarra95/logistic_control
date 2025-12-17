export type TipoProducto = 'GENERAL' | 'PELIGROSO' | 'PERECEDERO' | 'FRAGIL' | 'REFRIGERADO';

export interface Producto {
  id: number;
  codigo: string;
  descripcion: string;
  ncmArancel?: string;
  categoria?: string;
  tipo: TipoProducto;
  esPeligroso: boolean;
  esPerecedero: boolean;
  esFragil: boolean;
  requiereRefrigeracion: boolean;
  temperaturaMinimaC?: number;
  temperaturaMaximaC?: number;
  pesoUnitarioKg: number;
  volumenUnitarioM3: number;
  alto?: number;
  ancho?: number;
  largo?: number;
  unidadMedida: string;
  valorUnitario: number;
  moneda: string;
  numeroONU?: string;
  claseIMO?: string;
  grupoEmbalaje?: string;
  observaciones?: string;
}

export interface CreateProductoRequest extends Omit<Producto, 'id'> {}

export interface UpdateProductoRequest extends Partial<CreateProductoRequest> {}