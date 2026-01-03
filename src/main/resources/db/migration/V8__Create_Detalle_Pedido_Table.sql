-- Migración V8: Crear tabla detalle_pedido
-- Esta tabla almacena los ítems/detalles de cada pedido

CREATE TABLE detalle_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(15,2) NOT NULL CHECK (precio_unitario >= 0),
    sub_total NUMERIC(15,2) NOT NULL CHECK (sub_total >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    deletion_reason VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE RESTRICT
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_detalle_pedido_pedido_id ON detalle_pedido(pedido_id);
CREATE INDEX idx_detalle_pedido_producto_id ON detalle_pedido(producto_id);
CREATE INDEX idx_detalle_pedido_is_active ON detalle_pedido(is_active);

-- Comentarios para documentación
COMMENT ON TABLE detalle_pedido IS 'Detalle/ítems de los pedidos, contiene los productos y cantidades de cada pedido';
COMMENT ON COLUMN detalle_pedido.pedido_id IS 'Referencia al pedido padre';
COMMENT ON COLUMN detalle_pedido.producto_id IS 'Referencia al producto';
COMMENT ON COLUMN detalle_pedido.cantidad IS 'Cantidad del producto en este item';
COMMENT ON COLUMN detalle_pedido.precio_unitario IS 'Precio unitario del producto al momento del pedido';
COMMENT ON COLUMN detalle_pedido.sub_total IS 'Subtotal calculado (cantidad * precio_unitario)';