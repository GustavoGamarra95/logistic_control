export type TipoMovimiento = 'ENTRADA' | 'SALIDA' | 'RESERVA' | 'LIBERACION' | 'AJUSTE' | 'TRANSFERENCIA';
export type EstadoInventario = 'DISPONIBLE' | 'RESERVADO' | 'EN_CUARENTENA' | 'DAÑADO' | 'VENCIDO';

export interface Inventario {
  id: number;
  productoId: number;
  almacenId: number;
  almacenNombre?: string;
  ubicacion: string;
  lote?: string;
  fechaIngreso: string;
  fechaVencimiento?: string;
  cantidadDisponible: number;
  cantidadReservada: number;
  cantidadTotal: number;
  estado: EstadoInventario;
  costoDiarioAlmacenaje: number;
  costoTotalAlmacenaje: number;
  diasAlmacenados: number;
  observaciones?: string;
}

export interface MovimientoInventario {
  id: number;
  inventarioId: number;
  tipoMovimiento: TipoMovimiento;
  cantidad: number;
  usuarioId: number;
  usuarioNombre?: string;
  fechaMovimiento: string;
  referencia?: string;
  pedidoId?: number;
  observaciones?: string;
}

export interface CreateInventarioRequest extends Omit<Inventario, 'id' | 'cantidadReservada' | 'cantidadTotal' | 'costoTotalAlmacenaje' | 'diasAlmacenados' | 'almacenNombre'> {}

export interface CreateMovimientoRequest {
  inventarioId: number;
  tipoMovimiento: TipoMovimiento;
  cantidad: number;
  referencia?: string;
  pedidoId?: number;
  observaciones?: string;
}

export interface Almacen {
  id: number;
  codigo: string;
  nombre: string;
  direccion: string;
  ciudad: string;
  pais: string;
  capacidadM3: number;
  ocupacionM3: number;
  latitud?: number;
  longitud?: number;
  tieneRefrigeracion: boolean;
  temperaturaMinimaC?: number;
  temperaturaMaximaC?: number;
  activo: boolean;
}