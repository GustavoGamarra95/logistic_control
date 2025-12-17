create table proveedores
(
    id              bigserial
        primary key,
    nombre          varchar(255)                          not null,
    razon_social    varchar(255),
    ruc             varchar(500),
    tipo            varchar(255)                          not null,
    direccion       varchar(500),
    ciudad          varchar(100),
    pais            varchar(100),
    contacto        varchar(200),
    email           varchar(500),
    telefono        varchar(500),
    costo_servicio  double precision,
    moneda          varchar(10) default 'PYG'::character varying,
    plazo_pago_dias integer,
    cuenta_bancaria varchar(500),
    banco           varchar(100),
    calificacion    double precision,
    observaciones   varchar(1000),
    created_at      timestamp   default CURRENT_TIMESTAMP not null,
    updated_at      timestamp   default CURRENT_TIMESTAMP,
    deleted_at      timestamp,
    is_active       boolean     default true
);

comment on column proveedores.ruc is 'RUC del proveedor (cifrado AES-256)';

comment on column proveedores.email is 'Email del proveedor (cifrado AES-256)';

comment on column proveedores.telefono is 'Teléfono del proveedor (cifrado AES-256)';

comment on column proveedores.cuenta_bancaria is 'Cuenta bancaria del proveedor (cifrado AES-256)';

create index idx_proveedor_ruc
    on proveedores (ruc);

create index idx_proveedor_tipo
    on proveedores (tipo);

