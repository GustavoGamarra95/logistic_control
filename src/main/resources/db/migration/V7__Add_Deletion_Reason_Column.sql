-- Agregar columna deletion_reason a todas las tablas principales que existen
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS deletion_reason VARCHAR(500);
ALTER TABLE proveedores ADD COLUMN IF NOT EXISTS deletion_reason VARCHAR(500);
ALTER TABLE productos ADD COLUMN IF NOT EXISTS deletion_reason VARCHAR(500);
ALTER TABLE pedidos ADD COLUMN IF NOT EXISTS deletion_reason VARCHAR(500);
ALTER TABLE containers ADD COLUMN IF NOT EXISTS deletion_reason VARCHAR(500);
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS deletion_reason VARCHAR(500);
ALTER TABLE facturas ADD COLUMN IF NOT EXISTS deletion_reason VARCHAR(500);
ALTER TABLE inventario ADD COLUMN IF NOT EXISTS deletion_reason VARCHAR(500);
ALTER TABLE facturas_proveedor ADD COLUMN IF NOT EXISTS deletion_reason VARCHAR(500);
ALTER TABLE pagos ADD COLUMN IF NOT EXISTS deletion_reason VARCHAR(500);
