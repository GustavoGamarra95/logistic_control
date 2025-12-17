create table usuarios
(
    id                      bigserial
        primary key,
    username                varchar(50)                         not null
        unique,
    password                varchar(255)                        not null,
    nombre                  varchar(100)                        not null,
    apellido                varchar(100)                        not null,
    email                   varchar(500)                        not null
        unique,
    telefono                varchar(500),
    enabled                 boolean   default true,
    account_non_expired     boolean   default true,
    account_non_locked      boolean   default true,
    credentials_non_expired boolean   default true,
    last_login              timestamp,
    failed_login_attempts   integer   default 0,
    cliente_id              bigint
        constraint fk_usuario_cliente
            references clientes
            on delete set null,
    created_at              timestamp default CURRENT_TIMESTAMP not null,
    updated_at              timestamp default CURRENT_TIMESTAMP,
    deleted_at              timestamp,
    is_active               boolean   default true
);

comment on table usuarios is 'Tabla de usuarios del sistema con autenticación JWT';

comment on column usuarios.password is 'Contraseña hasheada con BCrypt';

comment on column usuarios.email is 'Email del usuario (cifrado AES-256)';

comment on column usuarios.telefono is 'Teléfono del usuario (cifrado AES-256)';

comment on column usuarios.last_login is 'Fecha y hora del último login exitoso';

comment on column usuarios.failed_login_attempts is 'Intentos fallidos de login (bloqueo automático a los 5)';

create index idx_usuarios_enabled
    on usuarios (enabled);

create index idx_usuarios_last_login
    on usuarios (last_login);

create index idx_usuarios_account_non_locked
    on usuarios (account_non_locked);

create index idx_usuario_email
    on usuarios (email);

create index idx_usuario_username
    on usuarios (username);

