create table clientes
(
    id                        bigserial
        primary key,
    razon_social              varchar(255)                        not null,
    nombre_fantasia           varchar(255),
    ruc                       varchar(500)                        not null
        unique,
    dv                        varchar(1),
    direccion                 varchar(500)                        not null,
    ciudad                    varchar(100),
    pais                      varchar(100)                        not null,
    contacto                  varchar(200),
    email                     varchar(500)                        not null
        unique,
    telefono                  varchar(500),
    celular                   varchar(500),
    tipo_servicio             varchar(255),
    credito_limite            double precision,
    credito_disponible        double precision,
    es_facturador_electronico boolean   default false,
    estado_ruc                varchar(20),
    observaciones             varchar(1000),
    created_at                timestamp default CURRENT_TIMESTAMP not null,
    updated_at                timestamp default CURRENT_TIMESTAMP,
    deleted_at                timestamp,
    is_active                 boolean   default true
);

comment on column clientes.ruc is 'RUC del cliente (cifrado AES-256)';

comment on column clientes.email is 'Email del cliente (cifrado AES-256)';

comment on column clientes.telefono is 'Teléfono del cliente (cifrado AES-256)';

comment on column clientes.celular is 'Celular del cliente (cifrado AES-256)';

create index idx_cliente_ruc
    on clientes (ruc);

create index idx_cliente_email
    on clientes (email);

