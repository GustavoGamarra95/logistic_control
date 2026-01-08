-- Migración V12: Agregar soporte para facturación parcial de pedidos
-- Permite facturar pedidos en múltiples facturas parciales con tracking de cantidades

-- 1. Agregar campo cantidad_facturada a detalle_pedido
ALTER TABLE detalle_pedido
ADD COLUMN cantidad_facturada INTEGER DEFAULT 0 NOT NULL;

-- 2. Agregar índice para mejorar performance en queries de facturación
CREATE INDEX idx_detalle_pedido_facturacion
ON detalle_pedido(pedido_id, cantidad, cantidad_facturada)
WHERE is_active = true;

-- 3. Agregar campo cantidad_pendiente como columna generada (computed column)
ALTER TABLE detalle_pedido
ADD COLUMN cantidad_pendiente INTEGER GENERATED ALWAYS AS (cantidad - cantidad_facturada) STORED;

-- 4. Agregar constraint para validar cantidad_facturada
ALTER TABLE detalle_pedido
ADD CONSTRAINT chk_cantidad_facturada
CHECK (cantidad_facturada >= 0 AND cantidad_facturada <= cantidad);

-- 5. El estado FACTURADO se agrega automáticamente porque estado es VARCHAR, no un enum en DB
-- No se requiere ALTER TYPE ya que no existe un tipo enum estado_pedido en la base de datos

-- 6. Agregar campo detalle_pedido_id a detalle_factura para rastrear origen de facturación
ALTER TABLE detalle_factura
ADD COLUMN detalle_pedido_id BIGINT;

-- 7. Crear constraint de foreign key
ALTER TABLE detalle_factura
ADD CONSTRAINT fk_detalle_factura_detalle_pedido
FOREIGN KEY (detalle_pedido_id) REFERENCES detalle_pedido(id) ON DELETE SET NULL;

-- 8. Crear índice en detalle_factura para mejorar queries
CREATE INDEX idx_detalle_factura_detalle_pedido
ON detalle_factura(detalle_pedido_id)
WHERE detalle_pedido_id IS NOT NULL;

-- 9. Migración de datos existentes: actualizar cantidad_facturada basado en facturas existentes
-- Solo se cuentan facturas activas y no anuladas/rechazadas
UPDATE detalle_pedido dp
SET cantidad_facturada = COALESCE(
    (SELECT SUM(df.cantidad)
     FROM detalle_factura df
     INNER JOIN facturas f ON df.factura_id = f.id
     WHERE df.producto_id = dp.producto_id
       AND f.pedido_id = dp.pedido_id
       AND f.is_active = true
       AND f.estado NOT IN ('ANULADA', 'RECHAZADA')
     GROUP BY df.producto_id),
    0
)
WHERE dp.is_active = true;

-- 10. Comentarios para documentación
COMMENT ON COLUMN detalle_pedido.cantidad_facturada IS 'Cantidad total facturada de este detalle de pedido. Suma de todas las facturas activas asociadas.';
COMMENT ON COLUMN detalle_pedido.cantidad_pendiente IS 'Cantidad pendiente de facturar. Columna generada: cantidad - cantidad_facturada.';
COMMENT ON COLUMN detalle_factura.detalle_pedido_id IS 'Referencia al detalle de pedido original. Permite rastrear facturación parcial.';
