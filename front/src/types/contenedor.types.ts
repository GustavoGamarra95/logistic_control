export type TipoContenedor = '20FT' | '40FT' | '40HC' | '45FT' | 'REEFER20' | 'REEFER40';
export type EstadoContenedor = 'DISPONIBLE' | 'EN_TRANSITO' | 'EN_PUERTO' | 'EN_DEPOSITO' | 'EN_CONSOLIDACION' | 'CERRADO' | 'DESPACHADO';

export interface Contenedor {
  id: number;
  numeroContenedor: string;
  tipo: TipoContenedor;
  estado: EstadoContenedor;
  capacidadKg: number;
  capacidadM3: number;
  pesoActualKg: number;
  volumenActualM3: number;
  porcentajeOcupacionPeso: number;
  porcentajeOcupacionVolumen: number;
  fechaLlegada?: string;
  fechaSalida?: string;
  ubicacionActual?: string;
  latitud?: number;
  longitud?: number;
  paisOrigen?: string;
  paisDestino?: string;
  puertoOrigen?: string;
  puertoDestino?: string;
  naviera?: string;
  numeroViaje?: string;
  observaciones?: string;
}

export interface CreateContenedorRequest extends Omit<Contenedor, 'id' | 'pesoActualKg' | 'volumenActualM3' | 'porcentajeOcupacionPeso' | 'porcentajeOcupacionVolumen'> {}

export interface UpdateContenedorRequest extends Partial<CreateContenedorRequest> {}

export interface ContenedorProducto {
  id: number;
  contenedorId: number;
  productoId: number;
  cantidad: number;
  pesoTotalKg: number;
  volumenTotalM3: number;
  posicion?: string;
}