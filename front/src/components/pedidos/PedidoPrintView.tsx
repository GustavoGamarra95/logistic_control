import { forwardRef } from 'react';
import { Pedido } from '@/types/pedido.types';
import { formatCurrency, formatDate, formatDateTime } from '@/utils/format';
import QRCode from 'react-qr-code';

interface PedidoPrintViewProps {
  pedido: Pedido;
}

export const PedidoPrintView = forwardRef<HTMLDivElement, PedidoPrintViewProps>(
  ({ pedido }, ref) => {
    const qrData = JSON.stringify({
      id: pedido.id,
      codigoTracking: pedido.codigoTracking,
      estado: pedido.estado,
      fechaRegistro: pedido.fechaRegistro,
    });

    return (
      <div ref={ref} className="print-container" style={{ padding: '40px', fontFamily: 'Arial, sans-serif' }}>
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '30px' }}>
          <div>
            <h1 style={{ margin: 0, fontSize: '28px', fontWeight: 'bold', color: '#1890ff' }}>
              PEDIDO DE IMPORTACIÓN/EXPORTACIÓN
            </h1>
            <p style={{ margin: '10px 0 0 0', fontSize: '14px', color: '#666' }}>
              Sistema de Control Logístico
            </p>
          </div>
          <div style={{ textAlign: 'center' }}>
            <QRCode value={qrData} size={120} />
            <p style={{ margin: '8px 0 0 0', fontSize: '11px', color: '#666' }}>
              Escanear para detalles
            </p>
          </div>
        </div>

        {/* Información Principal */}
        <div style={{
          backgroundColor: '#f5f5f5',
          padding: '20px',
          borderRadius: '8px',
          marginBottom: '25px',
          border: '2px solid #1890ff'
        }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
            <div>
              <p style={{ margin: '0 0 5px 0', fontSize: '12px', color: '#666', fontWeight: 'bold' }}>
                CÓDIGO DE TRACKING
              </p>
              <p style={{ margin: 0, fontSize: '20px', fontWeight: 'bold', fontFamily: 'monospace', color: '#1890ff' }}>
                {pedido.codigoTracking}
              </p>
            </div>
            <div>
              <p style={{ margin: '0 0 5px 0', fontSize: '12px', color: '#666', fontWeight: 'bold' }}>
                ESTADO ACTUAL
              </p>
              <p style={{ margin: 0, fontSize: '16px', fontWeight: 'bold' }}>
                {pedido.estado}
              </p>
            </div>
            <div>
              <p style={{ margin: '0 0 5px 0', fontSize: '12px', color: '#666', fontWeight: 'bold' }}>
                FECHA DE REGISTRO
              </p>
              <p style={{ margin: 0, fontSize: '14px' }}>
                {formatDateTime(pedido.fechaRegistro)}
              </p>
            </div>
            <div>
              <p style={{ margin: '0 0 5px 0', fontSize: '12px', color: '#666', fontWeight: 'bold' }}>
                CLIENTE
              </p>
              <p style={{ margin: 0, fontSize: '14px' }}>
                <strong>ID: {pedido.clienteId}</strong>
                {pedido.clienteNombre && (
                  <span style={{ display: 'block', marginTop: '3px', fontSize: '12px' }}>
                    {pedido.clienteNombre}
                  </span>
                )}
              </p>
            </div>
          </div>
        </div>

        {/* Ruta */}
        <div style={{ marginBottom: '25px' }}>
          <h3 style={{ fontSize: '16px', fontWeight: 'bold', borderBottom: '2px solid #1890ff', paddingBottom: '8px', marginBottom: '15px' }}>
            RUTA DE TRANSPORTE
          </h3>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr auto 1fr', gap: '20px', alignItems: 'center' }}>
            <div style={{ border: '1px solid #d9d9d9', padding: '15px', borderRadius: '8px' }}>
              <p style={{ margin: '0 0 8px 0', fontSize: '12px', color: '#666', fontWeight: 'bold' }}>ORIGEN</p>
              <p style={{ margin: '0 0 5px 0', fontSize: '14px', fontWeight: 'bold' }}>{pedido.paisOrigen}</p>
              {pedido.ciudadOrigen && <p style={{ margin: '0 0 3px 0', fontSize: '12px' }}>Ciudad: {pedido.ciudadOrigen}</p>}
              {pedido.puertoEmbarque && <p style={{ margin: 0, fontSize: '12px' }}>Puerto: {pedido.puertoEmbarque}</p>}
            </div>
            <div style={{ fontSize: '30px', color: '#1890ff' }}>→</div>
            <div style={{ border: '1px solid #d9d9d9', padding: '15px', borderRadius: '8px' }}>
              <p style={{ margin: '0 0 8px 0', fontSize: '12px', color: '#666', fontWeight: 'bold' }}>DESTINO</p>
              <p style={{ margin: '0 0 5px 0', fontSize: '14px', fontWeight: 'bold' }}>{pedido.paisDestino}</p>
              {pedido.ciudadDestino && <p style={{ margin: '0 0 3px 0', fontSize: '12px' }}>Ciudad: {pedido.ciudadDestino}</p>}
              {pedido.puertoDestino && <p style={{ margin: '0 0 3px 0', fontSize: '12px' }}>Puerto: {pedido.puertoDestino}</p>}
              {pedido.direccionEntrega && <p style={{ margin: 0, fontSize: '11px', color: '#666' }}>Dir: {pedido.direccionEntrega}</p>}
            </div>
          </div>
        </div>

        {/* Detalles de Carga */}
        <div style={{ marginBottom: '25px' }}>
          <h3 style={{ fontSize: '16px', fontWeight: 'bold', borderBottom: '2px solid #1890ff', paddingBottom: '8px', marginBottom: '15px' }}>
            DETALLES DE LA CARGA
          </h3>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
            <div>
              <p style={{ margin: '0 0 3px 0', fontSize: '11px', color: '#666' }}>Tipo de Carga:</p>
              <p style={{ margin: '0 0 10px 0', fontSize: '13px', fontWeight: 'bold' }}>{pedido.tipoCarga}</p>
            </div>
            <div>
              <p style={{ margin: '0 0 3px 0', fontSize: '11px', color: '#666' }}>Peso Total:</p>
              <p style={{ margin: '0 0 10px 0', fontSize: '13px', fontWeight: 'bold' }}>{pedido.pesoTotalKg} kg</p>
            </div>
            <div>
              <p style={{ margin: '0 0 3px 0', fontSize: '11px', color: '#666' }}>Volumen Total:</p>
              <p style={{ margin: '0 0 10px 0', fontSize: '13px', fontWeight: 'bold' }}>{pedido.volumenTotalM3} m³</p>
            </div>
            <div>
              <p style={{ margin: '0 0 3px 0', fontSize: '11px', color: '#666' }}>Valor Declarado:</p>
              <p style={{ margin: '0 0 10px 0', fontSize: '13px', fontWeight: 'bold' }}>
                {formatCurrency(pedido.valorDeclarado, pedido.moneda)}
              </p>
            </div>
            {pedido.numeroBlAwb && (
              <div>
                <p style={{ margin: '0 0 3px 0', fontSize: '11px', color: '#666' }}>N° BL/AWB:</p>
                <p style={{ margin: '0 0 10px 0', fontSize: '13px', fontFamily: 'monospace' }}>{pedido.numeroBlAwb}</p>
              </div>
            )}
            {pedido.numeroContenedorGuia && (
              <div>
                <p style={{ margin: '0 0 3px 0', fontSize: '11px', color: '#666' }}>N° Contenedor/Guía:</p>
                <p style={{ margin: '0 0 10px 0', fontSize: '13px', fontFamily: 'monospace' }}>{pedido.numeroContenedorGuia}</p>
              </div>
            )}
            {pedido.empresaTransporte && (
              <div style={{ gridColumn: '1 / -1' }}>
                <p style={{ margin: '0 0 3px 0', fontSize: '11px', color: '#666' }}>Empresa de Transporte:</p>
                <p style={{ margin: 0, fontSize: '13px' }}>{pedido.empresaTransporte}</p>
              </div>
            )}
            <div style={{ gridColumn: '1 / -1' }}>
              <p style={{ margin: '0 0 3px 0', fontSize: '11px', color: '#666' }}>Descripción de Mercadería:</p>
              <p style={{ margin: 0, fontSize: '13px' }}>{pedido.descripcionMercaderia}</p>
            </div>
          </div>
        </div>

        {/* Fechas */}
        {(pedido.fechaEstimadaLlegada || pedido.fechaLlegadaReal) && (
          <div style={{ marginBottom: '25px' }}>
            <h3 style={{ fontSize: '16px', fontWeight: 'bold', borderBottom: '2px solid #1890ff', paddingBottom: '8px', marginBottom: '15px' }}>
              FECHAS
            </h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
              {pedido.fechaEstimadaLlegada && (
                <div>
                  <p style={{ margin: '0 0 3px 0', fontSize: '11px', color: '#666' }}>Llegada Estimada:</p>
                  <p style={{ margin: 0, fontSize: '13px', fontWeight: 'bold' }}>{formatDate(pedido.fechaEstimadaLlegada)}</p>
                </div>
              )}
              {pedido.fechaLlegadaReal && (
                <div>
                  <p style={{ margin: '0 0 3px 0', fontSize: '11px', color: '#666' }}>Llegada Real:</p>
                  <p style={{ margin: 0, fontSize: '13px', fontWeight: 'bold' }}>{formatDate(pedido.fechaLlegadaReal)}</p>
                </div>
              )}
            </div>
          </div>
        )}

        {/* Resumen Financiero */}
        <div style={{ marginBottom: '25px' }}>
          <h3 style={{ fontSize: '16px', fontWeight: 'bold', borderBottom: '2px solid #1890ff', paddingBottom: '8px', marginBottom: '15px' }}>
            RESUMEN FINANCIERO
          </h3>
          <div style={{ backgroundColor: '#fafafa', padding: '15px', borderRadius: '8px' }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr auto', gap: '10px', marginBottom: '10px' }}>
              <p style={{ margin: 0, fontSize: '13px' }}>Subtotal:</p>
              <p style={{ margin: 0, fontSize: '13px', textAlign: 'right' }}>
                {formatCurrency(pedido.subTotal, pedido.moneda)}
              </p>
              <p style={{ margin: 0, fontSize: '13px' }}>IVA:</p>
              <p style={{ margin: 0, fontSize: '13px', textAlign: 'right' }}>
                {formatCurrency(pedido.iva, pedido.moneda)}
              </p>
              {pedido.requiereSeguro && pedido.valorSeguro && (
                <>
                  <p style={{ margin: 0, fontSize: '13px' }}>Seguro:</p>
                  <p style={{ margin: 0, fontSize: '13px', textAlign: 'right' }}>
                    {formatCurrency(pedido.valorSeguro, pedido.moneda)}
                  </p>
                </>
              )}
            </div>
            <div style={{ borderTop: '2px solid #1890ff', paddingTop: '10px', display: 'grid', gridTemplateColumns: '1fr auto', gap: '10px' }}>
              <p style={{ margin: 0, fontSize: '16px', fontWeight: 'bold' }}>TOTAL:</p>
              <p style={{ margin: 0, fontSize: '16px', fontWeight: 'bold', textAlign: 'right' }}>
                {formatCurrency(pedido.total, pedido.moneda)}
              </p>
            </div>
            {pedido.formaPago && (
              <p style={{ margin: '10px 0 0 0', fontSize: '12px', color: '#666' }}>
                Forma de Pago: <strong>{pedido.formaPago}</strong>
              </p>
            )}
          </div>
        </div>

        {/* Observaciones */}
        {pedido.observaciones && (
          <div style={{ marginBottom: '25px' }}>
            <h3 style={{ fontSize: '16px', fontWeight: 'bold', borderBottom: '2px solid #1890ff', paddingBottom: '8px', marginBottom: '15px' }}>
              OBSERVACIONES
            </h3>
            <p style={{ margin: 0, fontSize: '13px', whiteSpace: 'pre-wrap', backgroundColor: '#fafafa', padding: '15px', borderRadius: '8px' }}>
              {pedido.observaciones}
            </p>
          </div>
        )}

        {/* Footer */}
        <div style={{ marginTop: '40px', paddingTop: '20px', borderTop: '1px solid #d9d9d9', fontSize: '11px', color: '#666', textAlign: 'center' }}>
          <p style={{ margin: '0 0 5px 0' }}>
            Documento generado el {formatDateTime(new Date().toISOString())}
          </p>
          <p style={{ margin: 0 }}>
            Sistema de Control Logístico - Código de Tracking: {pedido.codigoTracking}
          </p>
        </div>

        {/* Print Styles */}
        <style>
          {`
            @media print {
              .print-container {
                padding: 20px;
              }
              body {
                print-color-adjust: exact;
                -webkit-print-color-adjust: exact;
              }
            }
          `}
        </style>
      </div>
    );
  }
);

PedidoPrintView.displayName = 'PedidoPrintView';
