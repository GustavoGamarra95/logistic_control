-- Migración V13: Crear módulo de devoluciones
-- Permite gestionar 3 tipos de devoluciones: productos físicos, correcciones de factura, y ajustes de pedido

-- 1. Crear tabla devoluciones_venta
CREATE TABLE devoluciones_venta (
    id BIGSERIAL PRIMARY KEY,
    numero_devolucion VARCHAR(50) UNIQUE,
    tipo VARCHAR(50) NOT NULL, -- PRODUCTO_FISICO, CORRECCION_FACTURA, AJUSTE_PEDIDO
    estado VARCHAR(50) NOT NULL DEFAULT 'SOLICITADA', -- SOLICITADA, EN_REVISION, APROBADA, RECHAZADA, EN_PROCESO, COMPLETADA, CANCELADA
    factura_id BIGINT,
    pedido_id BIGINT,
    cliente_id BIGINT NOT NULL,
    generar_nota_credito BOOLEAN DEFAULT FALSE,
    nota_credito_id BIGINT,
    fecha_solicitud TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_aprobacion TIMESTAMP,
    fecha_completada TIMESTAMP,
    subtotal NUMERIC(15,2),
    total_iva NUMERIC(15,2),
    total NUMERIC(15,2),
    motivo TEXT NOT NULL,
    observaciones TEXT,
    aprobado_por_usuario_id BIGINT,
    -- Campos de auditoría (BaseEntity)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    deletion_reason VARCHAR(500),

    CONSTRAINT fk_devolucion_factura FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE SET NULL,
    CONSTRAINT fk_devolucion_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE SET NULL,
    CONSTRAINT fk_devolucion_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE RESTRICT,
    CONSTRAINT fk_devolucion_nota_credito FOREIGN KEY (nota_credito_id) REFERENCES facturas(id) ON DELETE SET NULL,
    CONSTRAINT fk_devolucion_aprobado_por FOREIGN KEY (aprobado_por_usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- 2. Crear tabla detalle_devolucion
CREATE TABLE detalle_devolucion (
    id BIGSERIAL PRIMARY KEY,
    devolucion_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    detalle_factura_id BIGINT,
    detalle_pedido_id BIGINT,
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(15,2),
    descuento NUMERIC(15,2) DEFAULT 0,
    subtotal NUMERIC(15,2),
    porcentaje_iva INTEGER,
    monto_iva NUMERIC(15,2),
    total NUMERIC(15,2),
    estado_producto VARCHAR(50), -- BUENO, DANIADO, DEFECTUOSO
    inventario_entrada_id BIGINT,
    observaciones TEXT,
    -- Campos de auditoría (BaseEntity)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    deletion_reason VARCHAR(500),

    CONSTRAINT fk_detalle_devolucion_devolucion FOREIGN KEY (devolucion_id) REFERENCES devoluciones_venta(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_devolucion_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE RESTRICT,
    CONSTRAINT fk_detalle_devolucion_factura FOREIGN KEY (detalle_factura_id) REFERENCES detalle_factura(id) ON DELETE SET NULL,
    CONSTRAINT fk_detalle_devolucion_pedido FOREIGN KEY (detalle_pedido_id) REFERENCES detalle_pedido(id) ON DELETE SET NULL,
    CONSTRAINT fk_detalle_devolucion_inventario FOREIGN KEY (inventario_entrada_id) REFERENCES inventario(id) ON DELETE SET NULL,
    CONSTRAINT chk_detalle_devolucion_cantidad CHECK (cantidad > 0)
);

-- 3. Modificar tabla facturas para soportar notas de crédito
ALTER TABLE facturas ADD COLUMN tipo_factura VARCHAR(50) DEFAULT 'FACTURA_VENTA';
ALTER TABLE facturas ADD COLUMN factura_original_id BIGINT;
ALTER TABLE facturas ADD COLUMN devolucion_id BIGINT;

-- 4. Crear constraints de foreign key en facturas
ALTER TABLE facturas
ADD CONSTRAINT fk_factura_factura_original
FOREIGN KEY (factura_original_id) REFERENCES facturas(id) ON DELETE SET NULL;

ALTER TABLE facturas
ADD CONSTRAINT fk_factura_devolucion
FOREIGN KEY (devolucion_id) REFERENCES devoluciones_venta(id) ON DELETE SET NULL;

-- 5. Crear índices para mejorar performance
CREATE INDEX idx_devolucion_estado ON devoluciones_venta(estado) WHERE is_active = true;
CREATE INDEX idx_devolucion_tipo ON devoluciones_venta(tipo) WHERE is_active = true;
CREATE INDEX idx_devolucion_cliente ON devoluciones_venta(cliente_id) WHERE is_active = true;
CREATE INDEX idx_devolucion_factura ON devoluciones_venta(factura_id) WHERE factura_id IS NOT NULL;
CREATE INDEX idx_devolucion_pedido ON devoluciones_venta(pedido_id) WHERE pedido_id IS NOT NULL;
CREATE INDEX idx_devolucion_fecha ON devoluciones_venta(fecha_solicitud) WHERE is_active = true;

CREATE INDEX idx_detalle_devolucion_devolucion ON detalle_devolucion(devolucion_id);
CREATE INDEX idx_detalle_devolucion_producto ON detalle_devolucion(producto_id);

CREATE INDEX idx_factura_tipo ON facturas(tipo_factura) WHERE is_active = true;
CREATE INDEX idx_factura_original ON facturas(factura_original_id) WHERE factura_original_id IS NOT NULL;
CREATE INDEX idx_factura_devolucion ON facturas(devolucion_id) WHERE devolucion_id IS NOT NULL;

-- 6. Comentarios para documentación
COMMENT ON TABLE devoluciones_venta IS 'Gestión de devoluciones de ventas (productos, facturas, pedidos)';
COMMENT ON TABLE detalle_devolucion IS 'Ítems individuales de cada devolución';

COMMENT ON COLUMN devoluciones_venta.tipo IS 'Tipo: PRODUCTO_FISICO, CORRECCION_FACTURA, AJUSTE_PEDIDO';
COMMENT ON COLUMN devoluciones_venta.estado IS 'Estado: SOLICITADA, EN_REVISION, APROBADA, RECHAZADA, EN_PROCESO, COMPLETADA, CANCELADA';
COMMENT ON COLUMN devoluciones_venta.generar_nota_credito IS 'Si se genera automáticamente una nota de crédito al aprobar';
COMMENT ON COLUMN devoluciones_venta.nota_credito_id IS 'Referencia a la factura de tipo NOTA_CREDITO generada';

COMMENT ON COLUMN facturas.tipo_factura IS 'Tipo: FACTURA_VENTA, NOTA_CREDITO, NOTA_DEBITO';
COMMENT ON COLUMN facturas.factura_original_id IS 'Para notas de crédito: referencia a la factura original';
COMMENT ON COLUMN facturas.devolucion_id IS 'Si la factura es una nota de crédito, referencia a la devolución que la originó';

COMMENT ON COLUMN detalle_devolucion.estado_producto IS 'Estado del producto devuelto: BUENO, DANIADO, DEFECTUOSO';
COMMENT ON COLUMN detalle_devolucion.inventario_entrada_id IS 'Entrada de inventario generada al recibir devolución física';
