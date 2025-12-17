-- Agregar usuarios de prueba para todos los roles
-- Version: 1.0.2
-- Todos los usuarios tienen password: demo1234 (BCrypt hash con strength 12)

-- Usuario FINANZAS
INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('finanzas', '$2a$12$5ccqbuyj7vjfGN46pE3M5u1h6cF83nmiUCLVxpov5IAsZZ6VhqmYe',
        'María', 'Fernández', 'finanzas@logistic.com.py', '+595218888888', true, true, true, true);

INSERT INTO usuario_roles (usuario_id, role)
VALUES ((SELECT id FROM usuarios WHERE username = 'finanzas'), 'FINANZAS');

-- Usuario DEPOSITO
INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('deposito', '$2a$12$5ccqbuyj7vjfGN46pE3M5u1h6cF83nmiUCLVxpov5IAsZZ6VhqmYe',
        'Carlos', 'Ramírez', 'deposito@logistic.com.py', '+595219999999', true, true, true, true);

INSERT INTO usuario_roles (usuario_id, role)
VALUES ((SELECT id FROM usuarios WHERE username = 'deposito'), 'DEPOSITO');

-- Actualizar contraseña de usuarios existentes a demo1234 para consistencia
UPDATE usuarios
SET password = '$2a$12$5ccqbuyj7vjfGN46pE3M5u1h6cF83nmiUCLVxpov5IAsZZ6VhqmYe',
    enabled = true,
    account_non_expired = true,
    account_non_locked = true,
    credentials_non_expired = true
WHERE username IN ('admin', 'operador', 'cliente1');

COMMIT;
