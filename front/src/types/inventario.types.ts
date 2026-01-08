export type TipoMovimiento = 'ENTRADA' | 'SALIDA' | 'RESERVA' | 'LIBERACION' | 'AJUSTE' | 'TRANSFERENCIA';
export type EstadoInventario = 'DISPONIBLE' | 'RESERVADO' | 'EN_CUARENTENA' | 'DAÑADO' | 'VENCIDO';

export interface Inventario {
  id: number;
  clienteId?: number;
  clienteNombre?: string;
  productoId: number;
  productoDescripcion?: string;
  ubicacionDeposito?: string;
  zona?: string;
  pasillo?: string;
  rack?: string;
  nivel?: string;
  ubicacion: string; // Campo calculado que concatena todos los campos de ubicación
  lote?: string;
  fechaIngreso?: string;
  fechaEntrada?: string; // Alias para fechaIngreso
  fechaSalida?: string;
  fechaVencimiento?: string;
  cantidad: number;
  cantidadDisponible: number;
  cantidadReservada: number;
  estado: EstadoInventario;
  diasAlmacenaje?: number;
  costoAlmacenajeDiario?: number;
  costoAlmacenajeTotal?: number;
  observaciones?: string;
  createdAt?: string;
  updatedAt?: string;
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

export interface CreateInventarioRequest {
  clienteId: number;
  productoId: number;
  ubicacionDeposito?: string;
  zona?: string;
  pasillo?: string;
  rack?: string;
  nivel?: string;
  cantidad: number;
  lote?: string;
  fechaVencimiento?: string;
  costoAlmacenajeDiario?: number;
  observaciones?: string;
}

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