export type TipoProducto = 'GENERAL' | 'PELIGROSO' | 'PERECEDERO' | 'FRAGIL' | 'REFRIGERADO';

export interface Producto {
  id: number;
  codigo: string;
  descripcion: string;
  descripcionDetallada?: string;
  codigoNcm?: string;
  codigoArancel?: string;
  pesoKg: number;
  volumenM3?: number;
  unidadMedida?: string;
  cantidadPorUnidad?: number;
  paisOrigen?: string;
  valorUnitario?: number;
  moneda?: string;
  tasaIva?: number; // 0, 5 o 10
  precioVenta?: number; // Precio de venta al público
  esPeligroso?: boolean;
  esPerecedero?: boolean;
  esFragil?: boolean;
  requiereRefrigeracion?: boolean;
  temperaturaMin?: number;
  temperaturaMax?: number;
  observaciones?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateProductoRequest extends Omit<Producto, 'id'> {}

export interface UpdateProductoRequest extends Partial<CreateProductoRequest> {}