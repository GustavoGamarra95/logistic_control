-- Corregir contraseñas para cumplir validación de 8 caracteres mínimos
-- Nueva password para todos: demo1234 (8 caracteres)
-- Hash generado con BCrypt strength 12

-- Nota: Este hash fue generado con BCrypt(12) y corresponde a "demo1234"
-- Hash: $2a$12$5ccqbuyj7vjfGN46pE3M5u1h6cF83nmiUCLVxpov5IAsZZ6VhqmYe

-- Actualizar contraseñas de todos los usuarios de prueba
UPDATE usuarios
SET password = '$2a$12$5ccqbuyj7vjfGN46pE3M5u1h6cF83nmiUCLVxpov5IAsZZ6VhqmYe',
    enabled = true,
    account_non_expired = true,
    account_non_locked = true,
    credentials_non_expired = true,
    failed_login_attempts = 0
WHERE username IN ('admin', 'operador', 'cliente1', 'finanzas', 'deposito');

COMMIT;
