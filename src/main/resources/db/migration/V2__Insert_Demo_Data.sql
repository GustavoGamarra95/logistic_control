-- Datos de prueba para el sistema
-- Version: 1.0.0

-- Usuario admin por defecto (password: admin123)
-- Hash BCrypt de "admin123"
INSERT INTO usuarios (username, password, nombre, apellido, email, telefono)
VALUES ('admin', '$2a$10$XQxKlnCJZ5uj0ZyHmYq9IeGgE1X8SY.xN8LqQo9nK3Z3vKxYQmH3a',
        'Administrador', 'Sistema', 'admin@logistic.com.py', '+595211234567');

INSERT INTO usuario_roles (usuario_id, role)
VALUES (1, 'ADMIN');

-- Cliente de prueba
INSERT INTO clientes (razon_social, nombre_fantasia, ruc, dv, direccion, ciudad, pais,
                      contacto, email, telefono, tipo_servicio, credito_limite, credito_disponible,
                      es_facturador_electronico, estado_ruc)
VALUES
('IMPORTADORA EJEMPLO S.A.', 'IMPORT EJEMPLO', '80012345-6', '6',
 'Av. España 123', 'Asunción', 'Paraguay',
 'Juan Pérez', 'contacto@ejemplo.com.py', '+595211111111',
 'MARITIMO', 100000000.00, 100000000.00, true, 'ACT'),

('COMERCIAL DEMO S.R.L.', 'DEMO', '80023456-7', '7',
 'Av. Mcal. López 456', 'Asunción', 'Paraguay',
 'María González', 'info@demo.com.py', '+595212222222',
 'AEREO', 50000000.00, 50000000.00, true, 'ACT');

-- Usuario operador
INSERT INTO usuarios (username, password, nombre, apellido, email, telefono)
VALUES ('operador', '$2a$10$XQxKlnCJZ5uj0ZyHmYq9IeGgE1X8SY.xN8LqQo9nK3Z3vKxYQmH3a',
        'Pedro', 'Operador', 'operador@logistic.com.py', '+595213333333');

INSERT INTO usuario_roles (usuario_id, role)
VALUES (2, 'OPERADOR');

-- Usuario cliente
INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, cliente_id)
VALUES ('cliente1', '$2a$10$XQxKlnCJZ5uj0ZyHmYq9IeGgE1X8SY.xN8LqQo9nK3Z3vKxYQmH3a',
        'Juan', 'Pérez', 'juan.perez@ejemplo.com.py', '+595214444444', 1);

INSERT INTO usuario_roles (usuario_id, role)
VALUES (3, 'CLIENTE');

-- Productos de ejemplo
INSERT INTO productos (codigo, descripcion, codigo_ncm, peso_kg, volumen_m3,
                       unidad_medida, pais_origen, valor_unitario, moneda)
VALUES
('PROD001', 'Electrodomésticos - Refrigeradores', '84182100', 50.00, 0.5, 'UNIDAD', 'China', 2500000, 'PYG'),
('PROD002', 'Textiles - Ropa de vestir', '61099000', 5.00, 0.1, 'CAJA', 'China', 500000, 'PYG'),
('PROD003', 'Calzado deportivo', '64041100', 3.00, 0.05, 'PAR', 'Vietnam', 300000, 'PYG'),
('PROD004', 'Electrónicos - Notebooks', '84713000', 2.50, 0.02, 'UNIDAD', 'Taiwan', 3000000, 'PYG');

-- Proveedores
INSERT INTO proveedores (nombre, razon_social, tipo, pais, contacto, telefono,
                        costo_servicio, moneda, calificacion)
VALUES
('Transportes Paraguay S.A.', 'TRANSPORTES PY S.A.', 'TRANSPORTE', 'Paraguay',
 'Carlos Ruiz', '+595215555555', 5000000, 'PYG', 4.5),

('Agencia Aduanal Global', 'GLOBAL CUSTOMS S.R.L.', 'ADUANAL', 'Paraguay',
 'Ana López', '+595216666666', 2000000, 'PYG', 4.8),

('Depósito Central', 'DEPOSITO CENTRAL S.A.', 'ALMACENAJE', 'Paraguay',
 'Luis Martínez', '+595217777777', 100000, 'PYG', 4.3);

-- Container de ejemplo
INSERT INTO containers (numero, tipo, peso_maximo_kg, volumen_maximo_m3,
                       empresa_transporte, puerto_origen, puerto_destino,
                       fecha_salida, fecha_llegada_estimada, en_transito)
VALUES
('CONT001234567', 'CUARENTA_PIES', 28000, 67.7,
 'Maersk Line', 'Shanghai, China', 'Buenos Aires, Argentina',
 CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE + INTERVAL '5 days', true);

-- Asociar container con cliente
INSERT INTO container_clientes (container_id, cliente_id)
VALUES (1, 1);

COMMIT;
