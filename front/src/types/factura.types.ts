export type TipoFactura = 'CONTADO' | 'CREDITO';
export type EstadoFactura = 'BORRADOR' | 'EMITIDA' | 'ENVIADA_SIFEN' | 'APROBADA' | 'RECHAZADA' | 'ANULADA' | 'PAGADA' | 'VENCIDA';
export type EstadoPago = 'PENDIENTE' | 'PARCIAL' | 'PAGADO' | 'VENCIDO';

export interface Factura {
  id: number;
  numeroFactura: string;
  timbrado: string;
  puntoExpedicion: string;
  establecimiento: string;
  numeroDocumento: string;
  fechaEmision: string;
  fechaVencimiento?: string;
  clienteId: number;
  clienteRazonSocial?: string;
  clienteRuc?: string;
  tipo: TipoFactura;
  estado: EstadoFactura;
  estadoPago: EstadoPago;
  subtotal: number;
  iva5: number;
  iva10: number;
  ivaTotal: number;
  total: number;
  moneda: string;
  saldoPendiente: number;
  condicionPago?: string;
  observaciones?: string;
  // SIFEN fields
  cdc?: string;
  qrData?: string;
  xmlUrl?: string;
  pdfUrl?: string;
  estadoSifen?: string;
  fechaEnvioSifen?: string;
  mensajeSifen?: string;
}

export interface ItemFactura {
  id: number;
  facturaId: number;
  numeroItem: number;
  codigo?: string;
  descripcion: string;
  cantidad: number;
  unidadMedida: string;
  precioUnitario: number;
  tasaIva: number;
  montoIva: number;
  subtotal: number;
  total: number;
  pedidoId?: number;
}

export interface CreateFacturaRequest extends Omit<Factura, 'id' | 'numeroFactura' | 'cdc' | 'qrData' | 'xmlUrl' | 'pdfUrl' | 'estadoSifen' | 'fechaEnvioSifen' | 'mensajeSifen' | 'clienteRazonSocial' | 'clienteRuc' | 'saldoPendiente'> {
  items: Omit<ItemFactura, 'id' | 'facturaId' | 'montoIva' | 'subtotal' | 'total'>[];
}

export interface UpdateFacturaRequest extends Partial<Omit<CreateFacturaRequest, 'items'>> {
  items?: Omit<ItemFactura, 'id' | 'facturaId' | 'montoIva' | 'subtotal' | 'total'>[];
}

export interface PagoFactura {
  id: number;
  facturaId: number;
  fechaPago: string;
  monto: number;
  metodoPago: string;
  referencia?: string;
  observaciones?: string;
  usuarioId: number;
}

export interface SifenResponse {
  success: boolean;
  cdc?: string;
  estado?: string;
  mensaje?: string;
  qrData?: string;
  xmlUrl?: string;
  pdfUrl?: string;
}