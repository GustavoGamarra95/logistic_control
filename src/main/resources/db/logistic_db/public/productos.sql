create table productos
(
    id                     bigserial
        primary key,
    codigo                 varchar(50)                         not null
        unique,
    descripcion            varchar(500)                        not null,
    descripcion_detallada  varchar(2000),
    codigo_ncm             varchar(20),
    codigo_arancel         varchar(20),
    peso_kg                double precision                    not null,
    volumen_m3             double precision,
    unidad_medida          varchar(20),
    cantidad_por_unidad    integer,
    pais_origen            varchar(100),
    valor_unitario         double precision,
    moneda                 varchar(10),
    es_peligroso           boolean   default false,
    es_perecedero          boolean   default false,
    es_fragil              boolean   default false,
    requiere_refrigeracion boolean   default false,
    temperatura_min        double precision,
    temperatura_max        double precision,
    observaciones          varchar(1000),
    pedido_id              bigint
        constraint fk_producto_pedido
            references pedidos
            on delete set null,
    created_at             timestamp default CURRENT_TIMESTAMP not null,
    updated_at             timestamp default CURRENT_TIMESTAMP,
    deleted_at             timestamp,
    is_active              boolean   default true
);

create index idx_producto_codigo
    on productos (codigo);

create index idx_producto_ncm
    on productos (codigo_ncm);

