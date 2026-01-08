-- Agregar columna codigo a detalle_factura para almacenar código del producto
ALTER TABLE detalle_factura ADD COLUMN IF NOT EXISTS codigo VARCHAR(50);

COMMENT ON COLUMN detalle_factura.codigo IS 'Código del producto/ítem facturado';
