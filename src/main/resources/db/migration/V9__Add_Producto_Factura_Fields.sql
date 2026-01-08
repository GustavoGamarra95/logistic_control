-- V9: Agregar campos para facturación a la tabla productos
-- Fecha: 2026-01-03
-- Descripción: Agrega tasaIva y precioVenta para permitir facturación desde productos

-- Agregar columnas para facturación
ALTER TABLE productos ADD COLUMN IF NOT EXISTS tasa_iva INTEGER;
ALTER TABLE productos ADD COLUMN IF NOT EXISTS precio_venta DECIMAL(15,2);

-- Comentarios para documentación
COMMENT ON COLUMN productos.tasa_iva IS 'Tasa de IVA aplicable (0, 5 o 10%)';
COMMENT ON COLUMN productos.precio_venta IS 'Precio de venta al público';

-- Actualizar productos existentes con valores por defecto
UPDATE productos SET tasa_iva = 10 WHERE tasa_iva IS NULL;
UPDATE productos SET precio_venta = valor_unitario WHERE precio_venta IS NULL AND valor_unitario IS NOT NULL;
