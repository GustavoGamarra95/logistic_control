-- Agregar campos tipo y condicion_pago a facturas
ALTER TABLE facturas ADD COLUMN IF NOT EXISTS tipo VARCHAR(20);
ALTER TABLE facturas ADD COLUMN IF NOT EXISTS condicion_pago VARCHAR(200);

-- Actualizar facturas existentes con valores por defecto
UPDATE facturas SET tipo = 'CONTADO' WHERE tipo IS NULL;

COMMENT ON COLUMN facturas.tipo IS 'Tipo de factura: CONTADO o CREDITO';
COMMENT ON COLUMN facturas.condicion_pago IS 'Condición de pago para facturas a crédito';
