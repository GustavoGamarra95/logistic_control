create table facturas_proveedor
(
    id                bigserial
        primary key,
    proveedor_id      bigint                                not null
        references proveedores
            on delete restrict,
    numero_factura    varchar(50),
    fecha_emision     timestamp   default CURRENT_TIMESTAMP not null,
    fecha_vencimiento date,
    monto             double precision                      not null,
    iva               double precision,
    total             double precision                      not null,
    moneda            varchar(10) default 'PYG'::character varying,
    pagada            boolean     default false,
    fecha_pago        timestamp,
    metodo_pago       varchar(50),
    referencia_pago   varchar(100),
    concepto          varchar(500),
    observaciones     varchar(1000),
    created_at        timestamp   default CURRENT_TIMESTAMP not null,
    updated_at        timestamp   default CURRENT_TIMESTAMP,
    deleted_at        timestamp,
    is_active         boolean     default true
);

create index idx_factura_prov_proveedor
    on facturas_proveedor (proveedor_id);

create index idx_factura_prov_fecha
    on facturas_proveedor (fecha_emision);

create index idx_factura_prov_estado
    on facturas_proveedor (pagada);

