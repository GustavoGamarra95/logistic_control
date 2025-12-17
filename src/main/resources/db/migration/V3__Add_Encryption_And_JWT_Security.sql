-- Migración V3: Ajustar columnas para cifrado y agregar configuraciones JWT
-- Autor: Sistema
-- Fecha: 2025-01-22

-- Modificar longitud de columnas que serán cifradas en tabla clientes
ALTER TABLE clientes ALTER COLUMN ruc TYPE VARCHAR(500);
ALTER TABLE clientes ALTER COLUMN email TYPE VARCHAR(500);
ALTER TABLE clientes ALTER COLUMN telefono TYPE VARCHAR(500);
ALTER TABLE clientes ALTER COLUMN celular TYPE VARCHAR(500);

-- Modificar longitud de columnas que serán cifradas en tabla usuarios
ALTER TABLE usuarios ALTER COLUMN email TYPE VARCHAR(500);
ALTER TABLE usuarios ALTER COLUMN telefono TYPE VARCHAR(500);

-- Modificar longitud de columnas que serán cifradas en tabla proveedores
ALTER TABLE proveedores ALTER COLUMN ruc TYPE VARCHAR(500);
ALTER TABLE proveedores ALTER COLUMN email TYPE VARCHAR(500);
ALTER TABLE proveedores ALTER COLUMN telefono TYPE VARCHAR(500);
ALTER TABLE proveedores ALTER COLUMN cuenta_bancaria TYPE VARCHAR(500);

-- Agregar comentarios para documentar campos cifrados
COMMENT ON COLUMN clientes.ruc IS 'RUC del cliente (cifrado AES-256)';
COMMENT ON COLUMN clientes.email IS 'Email del cliente (cifrado AES-256)';
COMMENT ON COLUMN clientes.telefono IS 'Teléfono del cliente (cifrado AES-256)';
COMMENT ON COLUMN clientes.celular IS 'Celular del cliente (cifrado AES-256)';

COMMENT ON COLUMN usuarios.email IS 'Email del usuario (cifrado AES-256)';
COMMENT ON COLUMN usuarios.telefono IS 'Teléfono del usuario (cifrado AES-256)';

COMMENT ON COLUMN proveedores.ruc IS 'RUC del proveedor (cifrado AES-256)';
COMMENT ON COLUMN proveedores.email IS 'Email del proveedor (cifrado AES-256)';
COMMENT ON COLUMN proveedores.telefono IS 'Teléfono del proveedor (cifrado AES-256)';
COMMENT ON COLUMN proveedores.cuenta_bancaria IS 'Cuenta bancaria del proveedor (cifrado AES-256)';

-- Agregar índices para mejorar el rendimiento de consultas JWT
CREATE INDEX IF NOT EXISTS idx_usuarios_enabled ON usuarios(enabled);
CREATE INDEX IF NOT EXISTS idx_usuarios_last_login ON usuarios(last_login);
CREATE INDEX IF NOT EXISTS idx_usuarios_account_non_locked ON usuarios(account_non_locked);

-- Comentarios informativos
COMMENT ON TABLE usuarios IS 'Tabla de usuarios del sistema con autenticación JWT';
COMMENT ON COLUMN usuarios.password IS 'Contraseña hasheada con BCrypt';
COMMENT ON COLUMN usuarios.last_login IS 'Fecha y hora del último login exitoso';
COMMENT ON COLUMN usuarios.failed_login_attempts IS 'Intentos fallidos de login (bloqueo automático a los 5)';
