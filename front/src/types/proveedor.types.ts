export type TipoProveedor = 'NAVIERA' | 'AEROLINEA' | 'TRANSPORTISTA' | 'AGENTE_ADUANAL' | 'ALMACEN' | 'SEGURO' | 'OTRO';
export type CalificacionProveedor = 1 | 2 | 3 | 4 | 5;

export interface Proveedor {
  id: number;
  razonSocial: string;
  nombreFantasia?: string;
  ruc?: string;
  tipo: TipoProveedor;
  pais: string;
  ciudad?: string;
  direccion?: string;
  contacto?: string;
  email: string;
  telefono?: string;
  celular?: string;
  sitioWeb?: string;
  calificacion?: CalificacionProveedor;
  serviciosOfrecidos?: string;
  terminosPago?: string;
  activo: boolean;
  observaciones?: string;
}

export interface CreateProveedorRequest extends Omit<Proveedor, 'id' | 'calificacion'> {}

export interface UpdateProveedorRequest extends Partial<CreateProveedorRequest> {}