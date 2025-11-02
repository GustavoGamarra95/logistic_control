-- Sistema de Gestión Logística con Integración SIFEN
-- Version: 1.0.0
-- Initial Schema Migration

-- Tabla: usuarios
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(50),
    enabled BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP,
    failed_login_attempts INTEGER DEFAULT 0,
    cliente_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE usuario_roles (
    usuario_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (usuario_id, role),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla: clientes
CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    razon_social VARCHAR(255) NOT NULL,
    nombre_fantasia VARCHAR(255),
    ruc VARCHAR(20) UNIQUE NOT NULL,
    dv VARCHAR(1),
    direccion VARCHAR(500) NOT NULL,
    ciudad VARCHAR(100),
    pais VARCHAR(100) NOT NULL,
    contacto VARCHAR(200),
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(50),
    celular VARCHAR(50),
    tipo_servicio VARCHAR(50),
    credito_limite NUMERIC(15,2),
    credito_disponible NUMERIC(15,2),
    es_facturador_electronico BOOLEAN DEFAULT FALSE,
    estado_ruc VARCHAR(20),
    observaciones TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_cliente_ruc ON clientes(ruc);
CREATE INDEX idx_cliente_email ON clientes(email);

-- Tabla: productos
CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    descripcion_detallada TEXT,
    codigo_ncm VARCHAR(20),
    codigo_arancel VARCHAR(20),
    peso_kg NUMERIC(10,2) NOT NULL,
    volumen_m3 NUMERIC(10,3),
    unidad_medida VARCHAR(20),
    cantidad_por_unidad INTEGER,
    pais_origen VARCHAR(100),
    valor_unitario NUMERIC(15,2),
    moneda VARCHAR(10),
    es_peligroso BOOLEAN DEFAULT FALSE,
    es_perecedero BOOLEAN DEFAULT FALSE,
    es_fragil BOOLEAN DEFAULT FALSE,
    requiere_refrigeracion BOOLEAN DEFAULT FALSE,
    temperatura_min NUMERIC(5,2),
    temperatura_max NUMERIC(5,2),
    observaciones TEXT,
    pedido_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_producto_codigo ON productos(codigo);
CREATE INDEX idx_producto_ncm ON productos(codigo_ncm);

-- Tabla: containers
CREATE TABLE containers (
    id BIGSERIAL PRIMARY KEY,
    numero VARCHAR(50) UNIQUE NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    peso_kg NUMERIC(10,2),
    peso_maximo_kg NUMERIC(10,2),
    volumen_m3 NUMERIC(10,3),
    volumen_maximo_m3 NUMERIC(10,3),
    empresa_transporte VARCHAR(200),
    empresa_naviera VARCHAR(200),
    buque_nombre VARCHAR(200),
    viaje_numero VARCHAR(50),
    ruta VARCHAR(500),
    puerto_origen VARCHAR(100),
    puerto_destino VARCHAR(100),
    fecha_salida DATE,
    fecha_llegada_estimada DATE,
    fecha_llegada_real DATE,
    consolidado BOOLEAN DEFAULT FALSE,
    en_transito BOOLEAN DEFAULT FALSE,
    en_puerto BOOLEAN DEFAULT FALSE,
    en_aduana BOOLEAN DEFAULT FALSE,
    liberado BOOLEAN DEFAULT FALSE,
    numero_bl VARCHAR(100),
    fecha_emision_bl DATE,
    observaciones TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_container_numero ON containers(numero);
CREATE INDEX idx_container_fecha_salida ON containers(fecha_salida);

-- Tabla: pedidos
CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_carga VARCHAR(50),
    pais_origen VARCHAR(100) NOT NULL,
    pais_destino VARCHAR(100) NOT NULL,
    ciudad_origen VARCHAR(100),
    ciudad_destino VARCHAR(100),
    descripcion_mercaderia TEXT NOT NULL,
    numero_contenedor_guia VARCHAR(100),
    estado VARCHAR(50) NOT NULL DEFAULT 'REGISTRADO',
    codigo_tracking VARCHAR(50) UNIQUE,
    fecha_estimada_llegada DATE,
    fecha_llegada_real DATE,
    peso_total_kg NUMERIC(10,2),
    volumen_total_m3 NUMERIC(10,3),
    valor_declarado NUMERIC(15,2),
    moneda VARCHAR(10),
    numero_bl_awb VARCHAR(100),
    puerto_embarque VARCHAR(100),
    puerto_destino VARCHAR(100),
    empresa_transporte VARCHAR(200),
    observaciones TEXT,
    requiere_seguro BOOLEAN DEFAULT FALSE,
    valor_seguro NUMERIC(15,2),
    container_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE RESTRICT,
    FOREIGN KEY (container_id) REFERENCES containers(id) ON DELETE SET NULL
);

CREATE INDEX idx_pedido_tracking ON pedidos(codigo_tracking);
CREATE INDEX idx_pedido_estado ON pedidos(estado);
CREATE INDEX idx_pedido_fecha ON pedidos(fecha_registro);

-- Tabla: historial_estados
CREATE TABLE historial_estados (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    estado_anterior VARCHAR(50),
    estado_nuevo VARCHAR(50) NOT NULL,
    fecha_cambio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    comentario TEXT,
    usuario VARCHAR(100),
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE
);

CREATE INDEX idx_historial_pedido ON historial_estados(pedido_id);
CREATE INDEX idx_historial_fecha ON historial_estados(fecha_cambio);

-- Tabla: inventario
CREATE TABLE inventario (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    container_id BIGINT,
    producto_id BIGINT NOT NULL,
    pedido_id BIGINT,
    ubicacion_deposito VARCHAR(100),
    zona VARCHAR(50),
    pasillo VARCHAR(20),
    rack VARCHAR(20),
    nivel VARCHAR(20),
    cantidad INTEGER NOT NULL,
    cantidad_reservada INTEGER DEFAULT 0,
    cantidad_disponible INTEGER,
    estado VARCHAR(50) NOT NULL DEFAULT 'EN_TRANSITO',
    fecha_entrada TIMESTAMP,
    fecha_salida TIMESTAMP,
    lote VARCHAR(100),
    fecha_vencimiento TIMESTAMP,
    numero_declaracion_aduanal VARCHAR(100),
    fecha_despacho_aduana TIMESTAMP,
    dias_almacenaje INTEGER,
    costo_almacenaje_diario NUMERIC(10,2),
    costo_almacenaje_total NUMERIC(15,2),
    observaciones TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE RESTRICT,
    FOREIGN KEY (container_id) REFERENCES containers(id) ON DELETE SET NULL,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE RESTRICT,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE SET NULL
);

CREATE INDEX idx_inventario_cliente ON inventario(cliente_id);
CREATE INDEX idx_inventario_producto ON inventario(producto_id);
CREATE INDEX idx_inventario_estado ON inventario(estado);
CREATE INDEX idx_inventario_ubicacion ON inventario(ubicacion_deposito);

-- Tabla: facturas
CREATE TABLE facturas (
    id BIGSERIAL PRIMARY KEY,
    numero_factura VARCHAR(50) UNIQUE,
    fecha_emision TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento DATE,
    cliente_id BIGINT NOT NULL,
    pedido_id BIGINT,
    subtotal NUMERIC(15,2) NOT NULL,
    iva_5 NUMERIC(15,2),
    iva_10 NUMERIC(15,2),
    total_iva NUMERIC(15,2),
    total NUMERIC(15,2) NOT NULL,
    descuento NUMERIC(15,2) DEFAULT 0,
    moneda VARCHAR(10) DEFAULT 'PYG',
    tipo_cambio NUMERIC(10,4),
    estado VARCHAR(50) NOT NULL DEFAULT 'BORRADOR',
    cdc VARCHAR(44) UNIQUE,
    timbrado VARCHAR(20),
    establecimiento VARCHAR(10),
    punto_expedicion VARCHAR(10),
    tipo_documento VARCHAR(10) DEFAULT '1',
    fecha_envio_sifen TIMESTAMP,
    fecha_aprobacion_sifen TIMESTAMP,
    xml_de TEXT,
    xml_de_firmado TEXT,
    respuesta_sifen TEXT,
    codigo_estado_sifen VARCHAR(10),
    mensaje_sifen VARCHAR(500),
    qr_code TEXT,
    url_kude VARCHAR(500),
    saldo NUMERIC(15,2),
    pagado NUMERIC(15,2) DEFAULT 0,
    observaciones TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE RESTRICT,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE SET NULL
);

CREATE INDEX idx_factura_numero ON facturas(numero_factura);
CREATE INDEX idx_factura_cdc ON facturas(cdc);
CREATE INDEX idx_factura_cliente ON facturas(cliente_id);
CREATE INDEX idx_factura_estado ON facturas(estado);
CREATE INDEX idx_factura_fecha ON facturas(fecha_emision);

-- Tabla: detalle_factura
CREATE TABLE detalle_factura (
    id BIGSERIAL PRIMARY KEY,
    factura_id BIGINT NOT NULL,
    producto_id BIGINT,
    descripcion VARCHAR(500) NOT NULL,
    cantidad INTEGER NOT NULL,
    unidad_medida VARCHAR(20),
    precio_unitario NUMERIC(15,2) NOT NULL,
    descuento NUMERIC(15,2) DEFAULT 0,
    subtotal NUMERIC(15,2),
    porcentaje_iva INTEGER,
    monto_iva NUMERIC(15,2),
    total NUMERIC(15,2),
    codigo_ncm VARCHAR(20),
    observaciones VARCHAR(500),
    FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE SET NULL
);

-- Tabla: pagos
CREATE TABLE pagos (
    id BIGSERIAL PRIMARY KEY,
    factura_id BIGINT NOT NULL,
    monto NUMERIC(15,2) NOT NULL,
    fecha_pago TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodo_pago VARCHAR(50),
    referencia VARCHAR(100),
    banco VARCHAR(100),
    numero_cuenta VARCHAR(50),
    moneda VARCHAR(10) DEFAULT 'PYG',
    tipo_cambio NUMERIC(10,4),
    observaciones VARCHAR(500),
    FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE RESTRICT
);

CREATE INDEX idx_pago_factura ON pagos(factura_id);
CREATE INDEX idx_pago_fecha ON pagos(fecha_pago);

-- Tabla: proveedores
CREATE TABLE proveedores (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    razon_social VARCHAR(255),
    ruc VARCHAR(20),
    tipo VARCHAR(50) NOT NULL,
    direccion VARCHAR(500),
    ciudad VARCHAR(100),
    pais VARCHAR(100),
    contacto VARCHAR(200),
    email VARCHAR(100),
    telefono VARCHAR(50),
    costo_servicio NUMERIC(15,2),
    moneda VARCHAR(10) DEFAULT 'PYG',
    plazo_pago_dias INTEGER,
    cuenta_bancaria VARCHAR(100),
    banco VARCHAR(100),
    calificacion NUMERIC(2,1),
    observaciones TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_proveedor_ruc ON proveedores(ruc);
CREATE INDEX idx_proveedor_tipo ON proveedores(tipo);

-- Tabla: facturas_proveedor
CREATE TABLE facturas_proveedor (
    id BIGSERIAL PRIMARY KEY,
    proveedor_id BIGINT NOT NULL,
    numero_factura VARCHAR(50),
    fecha_emision TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento DATE,
    monto NUMERIC(15,2) NOT NULL,
    iva NUMERIC(15,2),
    total NUMERIC(15,2) NOT NULL,
    moneda VARCHAR(10) DEFAULT 'PYG',
    pagada BOOLEAN DEFAULT FALSE,
    fecha_pago TIMESTAMP,
    metodo_pago VARCHAR(50),
    referencia_pago VARCHAR(100),
    concepto VARCHAR(500),
    observaciones TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id) ON DELETE RESTRICT
);

CREATE INDEX idx_factura_prov_proveedor ON facturas_proveedor(proveedor_id);
CREATE INDEX idx_factura_prov_fecha ON facturas_proveedor(fecha_emision);
CREATE INDEX idx_factura_prov_estado ON facturas_proveedor(pagada);

-- Tablas de relación Many-to-Many

CREATE TABLE container_clientes (
    container_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    PRIMARY KEY (container_id, cliente_id),
    FOREIGN KEY (container_id) REFERENCES containers(id) ON DELETE CASCADE,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);

CREATE TABLE container_productos (
    container_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    PRIMARY KEY (container_id, producto_id),
    FOREIGN KEY (container_id) REFERENCES containers(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);

-- Foreign keys adicionales
ALTER TABLE usuarios ADD CONSTRAINT fk_usuario_cliente
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE SET NULL;

ALTER TABLE productos ADD CONSTRAINT fk_producto_pedido
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE SET NULL;
