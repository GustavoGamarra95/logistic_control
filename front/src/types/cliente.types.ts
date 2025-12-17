export type TipoServicio = 'AEREO' | 'MARITIMO' | 'TERRESTRE' | 'MULTIMODAL';

export interface Cliente {
  id: number;
  razonSocial: string;
  nombreFantasia?: string;
  ruc: string;
  dv: string;
  direccion: string;
  ciudad: string;
  pais: string;
  contacto?: string;
  email: string;
  telefono?: string;
  celular?: string;
  tipoServicio: TipoServicio;
  creditoLimite: number;
  creditoDisponible: number;
  esFacturadorElectronico: boolean;
  estadoRuc?: string;
  observaciones?: string;
}

export interface CreateClienteRequest extends Omit<Cliente, 'id' | 'creditoDisponible' | 'estadoRuc'> {}

export interface UpdateClienteRequest extends Partial<CreateClienteRequest> {}
