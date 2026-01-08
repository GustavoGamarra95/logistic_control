export type EstadoPedido =
  | 'REGISTRADO'
  | 'EN_TRANSITO'
  | 'RECIBIDO'
  | 'EN_ADUANA'
  | 'LIBERADO'
  | 'EN_DEPOSITO'
  | 'EN_REPARTO'
  | 'ENTREGADO'
  | 'CANCELADO'
  | 'DEVUELTO';

export type TipoCarga = 'FCL' | 'LCL' | 'GRANEL' | 'PERECEDERO' | 'PELIGROSO' | 'FRAGIL';

export interface Pedido {
  id: number;
  codigoTracking: string;
  fechaRegistro: string;
  clienteId: number;
  clienteNombre?: string;
  paisOrigen: string;
  paisDestino: string;
  ciudadOrigen?: string;
  ciudadDestino?: string;
  tipoCarga: TipoCarga;
  descripcionMercaderia: string;
  estado: EstadoPedido;
  fechaEstimadaLlegada?: string;
  fechaLlegadaReal?: string;
  pesoTotalKg: number;
  volumenTotalM3: number;
  valorDeclarado: number;
  moneda: string;
  numeroBlAwb?: string;
  puertoEmbarque?: string;
  puertoDestino?: string;
  empresaTransporte?: string;
  requiereSeguro: boolean;
  valorSeguro?: number;
  numeroContenedorGuia?: string;
  subTotal: number;
  iva: number;
  total: number;
  direccionEntrega?: string;
  formaPago?: string;
  containerId?: number;
  observaciones?: string;
}
