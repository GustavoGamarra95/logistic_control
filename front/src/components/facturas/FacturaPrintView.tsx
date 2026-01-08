import { forwardRef } from 'react';
import { Factura, ItemFactura } from '@/types/factura.types';
import { formatCurrency, formatDate, formatDateTime } from '@/utils/format';
import QRCode from 'react-qr-code';

interface FacturaPrintViewProps {
  factura: Factura;
  items: ItemFactura[];
}

export const FacturaPrintView = forwardRef<HTMLDivElement, FacturaPrintViewProps>(
  ({ factura, items }, ref) => {
    const qrData = factura.qrData || JSON.stringify({
      id: factura.id,
      numeroFactura: factura.numeroFactura,
      cdc: factura.cdc,
      total: factura.total,
    });

    return (
      <div ref={ref} className="print-container" style={{ padding: '40px', fontFamily: 'Arial, sans-serif', backgroundColor: 'white' }}>
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '30px', borderBottom: '3px solid #1890ff', paddingBottom: '20px' }}>
          <div>
            <h1 style={{ margin: 0, fontSize: '32px', fontWeight: 'bold', color: '#1890ff' }}>
              FACTURA
            </h1>
            <p style={{ margin: '5px 0 0 0', fontSize: '14px', color: '#666' }}>
              Documento Electrónico
            </p>
            <p style={{ margin: '3px 0 0 0', fontSize: '12px', color: '#999' }}>
              Sistema de Control Logístico
            </p>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div style={{ marginBottom: '10px' }}>
              <p style={{ margin: '0 0 5px 0', fontSize: '12px', color: '#666', fontWeight: 'bold' }}>
                N° FACTURA
              </p>
              <p style={{ margin: 0, fontSize: '24px', fontWeight: 'bold', fontFamily: 'monospace', color: '#1890ff' }}>
                {factura.numeroFactura}
              </p>
            </div>
            {factura.cdc && (
              <div style={{ textAlign: 'center', marginTop: '10px' }}>
                <QRCode value={qrData} size={100} />
              </div>
            )}
          </div>
        </div>

        {/* Información del Emisor y Timbrado */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '20px' }}>
          <div style={{ border: '1px solid #d9d9d9', padding: '15px', borderRadius: '4px', backgroundColor: '#fafafa' }}>
            <p style={{ margin: '0 0 10px 0', fontSize: '13px', color: '#666', fontWeight: 'bold' }}>
              DATOS DEL EMISOR
            </p>
            <p style={{ margin: '0 0 5px 0', fontSize: '14px', fontWeight: 'bold' }}>
              EMPRESA LOGISTICA S.A.
            </p>
            <p style={{ margin: '0 0 3px 0', fontSize: '12px' }}>
              RUC: 80000000-1
            </p>
            <p style={{ margin: 0, fontSize: '12px' }}>
              Asunción, Paraguay
            </p>
          </div>
          <div style={{ border: '1px solid #d9d9d9', padding: '15px', borderRadius: '4px', backgroundColor: '#fafafa' }}>
            <p style={{ margin: '0 0 10px 0', fontSize: '13px', color: '#666', fontWeight: 'bold' }}>
              INFORMACIÓN FISCAL
            </p>
            {factura.timbrado && (
              <p style={{ margin: '0 0 3px 0', fontSize: '12px' }}>
                <strong>Timbrado:</strong> {factura.timbrado}
              </p>
            )}
            {factura.establecimiento && (
              <p style={{ margin: '0 0 3px 0', fontSize: '12px' }}>
                <strong>Establecimiento:</strong> {factura.establecimiento}
              </p>
            )}
            {factura.puntoExpedicion && (
              <p style={{ margin: 0, fontSize: '12px' }}>
                <strong>Punto Expedición:</strong> {factura.puntoExpedicion}
              </p>
            )}
          </div>
        </div>

        {/* Información de Factura y Cliente */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '25px' }}>
          <div style={{ border: '2px solid #1890ff', padding: '15px', borderRadius: '4px' }}>
            <p style={{ margin: '0 0 10px 0', fontSize: '13px', color: '#666', fontWeight: 'bold' }}>
              DATOS DE LA FACTURA
            </p>
            <p style={{ margin: '0 0 5px 0', fontSize: '12px' }}>
              <strong>Tipo:</strong> {factura.tipo}
            </p>
            <p style={{ margin: '0 0 5px 0', fontSize: '12px' }}>
              <strong>Fecha Emisión:</strong> {formatDate(factura.fechaEmision)}
            </p>
            {factura.fechaVencimiento && (
              <p style={{ margin: '0 0 5px 0', fontSize: '12px' }}>
                <strong>Vencimiento:</strong> {formatDate(factura.fechaVencimiento)}
              </p>
            )}
            <p style={{ margin: 0, fontSize: '12px' }}>
              <strong>Moneda:</strong> {factura.moneda}
            </p>
          </div>
          <div style={{ border: '2px solid #1890ff', padding: '15px', borderRadius: '4px' }}>
            <p style={{ margin: '0 0 10px 0', fontSize: '13px', color: '#666', fontWeight: 'bold' }}>
              DATOS DEL CLIENTE
            </p>
            <p style={{ margin: '0 0 5px 0', fontSize: '12px', fontWeight: 'bold' }}>
              {factura.clienteRazonSocial}
            </p>
            {factura.clienteRuc && (
              <p style={{ margin: '0 0 5px 0', fontSize: '12px' }}>
                <strong>RUC:</strong> {factura.clienteRuc}
              </p>
            )}
            {factura.condicionPago && (
              <p style={{ margin: 0, fontSize: '12px' }}>
                <strong>Condición:</strong> {factura.condicionPago}
              </p>
            )}
          </div>
        </div>

        {/* CDC si existe */}
        {factura.cdc && (
          <div style={{ backgroundColor: '#f0f5ff', padding: '10px 15px', borderRadius: '4px', marginBottom: '20px' }}>
            <p style={{ margin: '0 0 5px 0', fontSize: '11px', color: '#666', fontWeight: 'bold' }}>
              CÓDIGO DE CONTROL (CDC)
            </p>
            <p style={{ margin: 0, fontSize: '11px', fontFamily: 'monospace', wordBreak: 'break-all' }}>
              {factura.cdc}
            </p>
          </div>
        )}

        {/* Items Table */}
        <div style={{ marginBottom: '25px' }}>
          <h3 style={{ fontSize: '14px', fontWeight: 'bold', borderBottom: '2px solid #1890ff', paddingBottom: '8px', marginBottom: '10px' }}>
            DETALLE DE LA FACTURA
          </h3>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '11px' }}>
            <thead>
              <tr style={{ backgroundColor: '#f5f5f5', borderBottom: '2px solid #d9d9d9' }}>
                <th style={{ padding: '10px 8px', textAlign: 'center', fontWeight: 'bold', width: '40px' }}>#</th>
                <th style={{ padding: '10px 8px', textAlign: 'left', fontWeight: 'bold', width: '80px' }}>Código</th>
                <th style={{ padding: '10px 8px', textAlign: 'left', fontWeight: 'bold' }}>Descripción</th>
                <th style={{ padding: '10px 8px', textAlign: 'center', fontWeight: 'bold', width: '60px' }}>Cant.</th>
                <th style={{ padding: '10px 8px', textAlign: 'center', fontWeight: 'bold', width: '60px' }}>Unidad</th>
                <th style={{ padding: '10px 8px', textAlign: 'right', fontWeight: 'bold', width: '90px' }}>P. Unit.</th>
                <th style={{ padding: '10px 8px', textAlign: 'center', fontWeight: 'bold', width: '50px' }}>IVA</th>
                <th style={{ padding: '10px 8px', textAlign: 'right', fontWeight: 'bold', width: '100px' }}>Total</th>
              </tr>
            </thead>
            <tbody>
              {items.map((item, index) => (
                <tr key={item.id || index} style={{ borderBottom: '1px solid #e8e8e8' }}>
                  <td style={{ padding: '8px', textAlign: 'center' }}>{item.numeroItem || index + 1}</td>
                  <td style={{ padding: '8px' }}>{item.codigo || '-'}</td>
                  <td style={{ padding: '8px' }}>{item.descripcion}</td>
                  <td style={{ padding: '8px', textAlign: 'center' }}>{item.cantidad}</td>
                  <td style={{ padding: '8px', textAlign: 'center' }}>{item.unidadMedida}</td>
                  <td style={{ padding: '8px', textAlign: 'right', fontFamily: 'monospace' }}>
                    {formatCurrency(item.precioUnitario, factura.moneda)}
                  </td>
                  <td style={{ padding: '8px', textAlign: 'center' }}>{item.tasaIva}%</td>
                  <td style={{ padding: '8px', textAlign: 'right', fontFamily: 'monospace', fontWeight: 'bold' }}>
                    {formatCurrency(item.total || (item.cantidad * item.precioUnitario * (1 + item.tasaIva / 100)), factura.moneda)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Observaciones y Totales */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 300px', gap: '20px', marginBottom: '30px' }}>
          <div>
            {factura.observaciones && (
              <div>
                <p style={{ margin: '0 0 8px 0', fontSize: '12px', fontWeight: 'bold', color: '#666' }}>
                  OBSERVACIONES:
                </p>
                <p style={{ margin: 0, fontSize: '11px', whiteSpace: 'pre-wrap', lineHeight: '1.5' }}>
                  {factura.observaciones}
                </p>
              </div>
            )}
          </div>
          <div style={{ border: '2px solid #1890ff', padding: '15px', borderRadius: '4px', backgroundColor: '#fafafa' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
              <span style={{ fontSize: '12px' }}>Subtotal:</span>
              <span style={{ fontSize: '12px', fontFamily: 'monospace', fontWeight: 'bold' }}>
                {formatCurrency(factura.subtotal, factura.moneda)}
              </span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '5px' }}>
              <span style={{ fontSize: '11px', color: '#666' }}>IVA 5%:</span>
              <span style={{ fontSize: '11px', fontFamily: 'monospace' }}>
                {formatCurrency(factura.iva5, factura.moneda)}
              </span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '5px' }}>
              <span style={{ fontSize: '11px', color: '#666' }}>IVA 10%:</span>
              <span style={{ fontSize: '11px', fontFamily: 'monospace' }}>
                {formatCurrency(factura.iva10, factura.moneda)}
              </span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '12px', paddingBottom: '10px', borderBottom: '1px solid #d9d9d9' }}>
              <span style={{ fontSize: '11px', color: '#666' }}>IVA Total:</span>
              <span style={{ fontSize: '11px', fontFamily: 'monospace', fontWeight: 'bold' }}>
                {formatCurrency(factura.ivaTotal, factura.moneda)}
              </span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', padding: '10px', backgroundColor: '#e6f7ff', borderRadius: '4px' }}>
              <span style={{ fontSize: '16px', fontWeight: 'bold' }}>TOTAL:</span>
              <span style={{ fontSize: '18px', fontFamily: 'monospace', fontWeight: 'bold', color: '#1890ff' }}>
                {formatCurrency(factura.total, factura.moneda)}
              </span>
            </div>
            {factura.tipo === 'CREDITO' && factura.saldoPendiente !== undefined && factura.saldoPendiente > 0 && (
              <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '10px', padding: '8px', backgroundColor: '#fff7e6', borderRadius: '4px' }}>
                <span style={{ fontSize: '12px', fontWeight: 'bold' }}>Saldo Pendiente:</span>
                <span style={{ fontSize: '13px', fontFamily: 'monospace', fontWeight: 'bold', color: '#fa8c16' }}>
                  {formatCurrency(factura.saldoPendiente, factura.moneda)}
                </span>
              </div>
            )}
          </div>
        </div>

        {/* Footer */}
        <div style={{ marginTop: '40px', paddingTop: '20px', borderTop: '1px solid #d9d9d9', fontSize: '10px', color: '#999', textAlign: 'center' }}>
          <p style={{ margin: '0 0 5px 0' }}>
            Este documento es una representación impresa de la Factura Electrónica
          </p>
          {factura.cdc && (
            <p style={{ margin: 0 }}>
              Para verificar la validez de este documento, ingrese a la SET con el CDC proporcionado
            </p>
          )}
          <p style={{ margin: '10px 0 0 0' }}>
            Generado: {formatDateTime(new Date().toISOString())}
          </p>
        </div>
      </div>
    );
  }
);

FacturaPrintView.displayName = 'FacturaPrintView';
